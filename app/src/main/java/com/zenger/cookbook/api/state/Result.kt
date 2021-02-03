package com.zenger.cookbook.api.state

sealed class Result<T> {

    data class Progress<T>(var loading: Boolean) : Result<T>()
    data class Success<T>(var data: T) : Result<T>()
    data class Failure<T>(val e: Throwable) : Result<T>()


    companion object {

        fun <T> loading(isLoading: Boolean): Result<T> = Progress(isLoading)

        fun <T> success(data: T): Result<T> = Success(data)

        fun <T> failure(e: Throwable): Result<T> = Failure(e)
    }
}