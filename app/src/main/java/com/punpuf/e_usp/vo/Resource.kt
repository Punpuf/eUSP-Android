package com.punpuf.e_usp.vo

sealed class Resource<T>(
    val status: Status,
    val data: T? = null,
    val errorCode: Int? = null,
) {
    class Success<T>(data: T) : Resource<T>(Status.SUCCESS, data, null)
    class Loading<T>(data: T? = null) : Resource<T>(Status.LOADING, data, null)
    class Error<T>(errorCode: Int, data: T? = null) : Resource<T>(Status.ERROR, data, errorCode)

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
    }
}