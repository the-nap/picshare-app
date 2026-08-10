package com.picshare.app.api.network

sealed class ApiResult<out T>{
  data class Success<T>(val data: T): ApiResult<T>()
  data class Failure(val error: AppError): ApiResult<Nothing>()
}

inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
  if(this is ApiResult.Success) block(data)
  return this
}

inline fun <T> ApiResult<T>.onFailure(block: (AppError) -> Unit): ApiResult<T> {
  if(this is ApiResult.Failure) block(error)
  return this
}

fun AppError.toUserMessage(): String = when (this) {
  AppError.Network -> "No internet connection"
  AppError.Timeout -> "Request timed out"
  AppError.EmptyBody -> "Empty server response"
  is AppError.Http -> "Server error ($code)"
  is AppError.Unknown -> "Something went wrong"
}

sealed class AppError{
  object Network: AppError()
  object Timeout: AppError()
  object EmptyBody: AppError()
  data class Http(val code: Int, val message: String?): AppError()
  data class Unknown(val throwable: Throwable): AppError()
}
