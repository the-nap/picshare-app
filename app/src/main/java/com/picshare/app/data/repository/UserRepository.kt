package com.picshare.app.data.repository

import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.api.network.Util.handleRequest
import com.picshare.app.data.model.UserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserRepository (
  private val dataSource: PicshareApi,
  private val gson: Gson,
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

  suspend fun contains(username: String, offset: Number, max: Number): NetworkResult<List<UserModel>>{
    return handleRequest(dispatcher){
      dataSource.contains(username, offset, max)
    }
  }

  suspend fun follows(id: String): NetworkResult<Boolean>{
    return handleRequest(dispatcher){
      dataSource.follows(id)
    }
  }

  suspend fun follow(id: String): NetworkResult<Unit>{
    return handleRequest(dispatcher){
      dataSource.follow(mapOf("toFollow" to id))
    }
  }

  suspend fun unfollow(id: String): NetworkResult<Unit>{
    return handleRequest(dispatcher){
      dataSource.unfollow(mapOf("toUnfollow" to id))
    }
  }
}