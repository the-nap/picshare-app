package com.picshare.app.ui.user

import android.icu.text.StringSearch
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util.handleRequest
import com.picshare.app.user.UserModel

class UserRepository (
  private val dataSource: PicshareApi
){

  suspend fun getUser(id: String): UserModel{
    return handleRequest{
      dataSource.getUser(id)
    }
  }

  suspend fun getByUsername(username: String): UserModel{
    return handleRequest {
      dataSource.getByUsername(username)
    }
  }
}