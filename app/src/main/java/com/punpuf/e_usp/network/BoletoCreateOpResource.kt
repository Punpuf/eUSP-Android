package com.punpuf.e_usp.network

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.vo.Resource
import com.punpuf.e_usp.vo_network.NetworkResponseGenerateBoleto
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

abstract class BoletoCreateOpResource<ResultType, RequestType> {

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

                    try {
                        networkCreateBoleto(dbSource)
                    }
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

    private suspend fun networkCreateBoleto(dbSource: LiveData<ResultType>) {
        val createBoletoResponse = createBoletoCall()

        result.addSource(createBoletoResponse) { response ->
            result.removeSource(createBoletoResponse)

            if (response is ApiSuccessResponse && response.body.hasError == false && response.body.id != null) {
                CoroutineScope(coroutineContextIo).launch {
                    saveCreatedBoletoResult(response.body)
                    CoroutineScope(coroutineContextMain).launch {
                        result.addSource(loadFromDb()) { newDbData ->
                            setValue(Resource.Success(newDbData))
                        }
                    }
                }
            } else {
                CoroutineScope(coroutineContextMain).launch {
                    fetchFromNetwork(dbSource)
                }
            }
        }
    }

    private suspend fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()

        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)

            when (response) {
                is ApiSuccessResponse -> {
                    CoroutineScope(coroutineContextIo).launch {
                        saveCallResult(processResponse(response))
                        CoroutineScope(coroutineContextMain).launch {
                            result.addSource(loadFromDb()) { newDbData ->
                                setValue(Resource.Success(newDbData))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    CoroutineScope(coroutineContextMain).launch {
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
    protected abstract suspend fun createBoletoCall(): LiveData<ApiResponse<NetworkResponseGenerateBoleto>>

    @MainThread
    protected abstract suspend fun createCall(): LiveData<ApiResponse<RequestType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract suspend fun saveCallResult(result: RequestType)

    @WorkerThread
    protected abstract suspend fun saveCreatedBoletoResult(result: NetworkResponseGenerateBoleto)

}