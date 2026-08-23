package com.picshare.app.api.network

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object Util {
  private val TAG = this.javaClass.simpleName

  suspend fun <T> handleRequest(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
  ): NetworkResult<T> {
    return withContext(dispatcher) {
      try {
        NetworkResult.Success(apiCall.invoke())
      } catch (throwable: Throwable) {
        Log.e(TAG, throwable.message, throwable)
        when (throwable) {
          is IOException -> NetworkResult.Error("Check your internet connection")
          is HttpException -> {
            val errorResponse = convertErrorBody(throwable)
            NetworkResult.Error(errorResponse?.message ?: "Server error: ${throwable.code()}")
          }
          else -> {
            NetworkResult.Error(throwable.message ?: "An unknown error occurred")
          }
        }
      }
    }
  }

  private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
      throwable.response()?.errorBody()?.string()?.let {
        Gson().fromJson(it, ErrorResponse::class.java)
      }
    } catch (exception: Exception) {
      Log.e(TAG, exception.message, exception)
      null
    }
  }

  data class ErrorResponse(val message: String?)

  sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
  }
}
