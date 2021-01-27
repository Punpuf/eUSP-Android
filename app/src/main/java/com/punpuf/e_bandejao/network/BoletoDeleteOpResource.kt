package com.punpuf.e_bandejao.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.punpuf.e_bandejao.Const
import com.punpuf.e_bandejao.vo.Resource
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

abstract class BoletoDeleteOpResource<ResultType, RequestType> {

    private val result = MediatorLiveData<Resource<ResultType>>()
    private val supervisorJob = SupervisorJob()
    private val coroutineContextMain = Dispatchers.Main
    private val coroutineContextIo = Dispatchers.IO

    suspend fun build(): MediatorLiveData<Resource<ResultType>> {
        coroutineContextMain + supervisorJob

        withContext(Dispatchers.Main) {

            val dbSource = loadFromDb()
            result.addSource(dbSource) {

                result.removeSource(dbSource)
                setValue(Resource.Loading(it))
                CoroutineScope(coroutineContextMain).launch {

                    try { networkDeleteBoleto(dbSource) }
                    catch (exception: Exception) {
                        Timber.e("Network Exception: $exception")
                        result.removeSource(dbSource)
                        result.addSource(dbSource) { newDbData ->
                            setValue(Resource.Error(Const.ERROR_NETWORK, newDbData))
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

    private suspend fun networkDeleteBoleto(dbSource: LiveData<ResultType>) {
        val deleteResponse = createDeleteCall()
        result.addSource(deleteResponse) {
            result.removeSource(deleteResponse)

            CoroutineScope(coroutineContextMain).launch {
                fetchFromNetwork(dbSource)
            }
        }
    }

    private suspend fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createGetCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newDbData -> setValue(Resource.Loading(newDbData)) }

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
                }
            }
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @MainThread
    protected abstract suspend fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract suspend fun createGetCall(): LiveData<ApiResponse<RequestType>>

    @MainThread
    protected abstract suspend fun createDeleteCall(): LiveData<ApiResponse<String>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract suspend fun saveCallResult(result: RequestType)

}