package com.picshare.app.api.network

object Util {

  suspend fun <T> handleRequest(request: suspend () -> T): T {
    return request()
  }
}