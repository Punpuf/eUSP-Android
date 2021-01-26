package com.punpuf.e_bandejao.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.punpuf.e_bandejao.Const.Companion.ERROR_NETWORK
import com.punpuf.e_bandejao.vo.Resource
import kotlinx.coroutines.*
import timber.log.Timber.e
import java.lang.Exception

abstract class NetworkBoundResource<ResultType, RequestType> {

    private val result = MediatorLiveData<Resource<ResultType>>()
    private val supervisorJob = SupervisorJob()
    private val coroutineContextMain = Dispatchers.Main
    private val coroutineContextIo = Dispatchers.IO

    var numNetworkRetries = 0

    suspend fun build(): MediatorLiveData<Resource<ResultType>> {
        coroutineContextMain + supervisorJob

        withContext(Dispatchers.Main) {
            setValue(Resource.Loading(null))

            val dbSource = loadFromDb()
            result.addSource(dbSource) { dbData ->

                result.removeSource(dbSource)
                CoroutineScope(coroutineContextMain).launch {
                    // try to update the data
                    if (shouldFetch(dbData)) {
                        try {
                            fetchFromNetwork(dbSource)
                        }
                        catch (exception: Exception) {
                            e("Network Exception: $exception")
                            result.removeSource(dbSource)
                            result.addSource(dbSource) { newDbData ->
                                setValue(Resource.Error(ERROR_NETWORK, newDbData))
                            }
                        }
                        // data is good, just show it from db
                    } else {
                        result.addSource(dbSource) { newDbData ->
                            setValue(Resource.Success(newDbData))
                        }
                    }
                }
            }
        }

        return result
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private suspend fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newDbData ->
            setValue(Resource.Loading(newDbData))
        }

        result.addSource(apiResponse) { response ->

            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {
                    CoroutineScope(coroutineContextIo).launch {
                        saveCallResult(processResponse(response))
                        CoroutineScope(coroutineContextMain).launch {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb()) { newDbData ->
                                setValue(Resource.Success(newDbData))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    CoroutineScope(coroutineContextMain).launch {
                        // reload from disk whatever we had
                        result.addSource(loadFromDb()) { newDbData ->
                            setValue(Resource.Success(newDbData))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    result.addSource(dbSource) { newDbData ->
                        setValue(Resource.Error(response.errorCode, newDbData))
                    }

                    CoroutineScope(coroutineContextMain).launch {
                        onFetchFailed()
                    }
                }
            }
        }
    }


    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @MainThread
    protected abstract suspend fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract suspend fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract suspend fun createCall(): LiveData<ApiResponse<RequestType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract suspend fun saveCallResult(result: RequestType)

    protected open suspend fun onFetchFailed() {}
}