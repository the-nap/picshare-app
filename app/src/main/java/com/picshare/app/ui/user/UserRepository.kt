package com.picshare.app.ui.user

import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.api.network.Util.handleRequest
import com.picshare.app.user.UserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserRepository (
  private val dataSource: PicshareApi,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
){

  suspend fun getUser(id: String): NetworkResult<UserModel> {
    return handleRequest(dispatcher) {
      dataSource.getUser(id)
    }
  }

  suspend fun getByUsername(username: String): NetworkResult<UserModel> {
    return handleRequest(dispatcher) {
      dataSource.getByUsername(username)
    }
  }
}