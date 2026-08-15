package com.picshare.app.data.repository

import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util
import com.picshare.app.data.model.UserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserRepository (
  private val dataSource: PicshareApi,
  private val gson: Gson,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
){

  suspend fun getUser(id: String): Util.NetworkResult<UserModel> {
    return Util.handleRequest(dispatcher) {
      dataSource.getUser(id)
    }
  }

  suspend fun getByUsername(username: String): Util.NetworkResult<UserModel> {
    return Util.handleRequest(dispatcher) {
      dataSource.getByUsername(username)
    }
  }
}