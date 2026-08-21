package com.picshare.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.api.network.Util.handleRequest
import com.picshare.app.data.model.UserModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class UserRepository (
  private val dataSource: PicshareApi,
  private val gson: Gson,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
  private val contentResolver: ContentResolver
){

  private val TAG = this.javaClass.simpleName
  private var cachedUser: UserModel? = null

  val currentUser: UserModel
    get() = cachedUser
      ?: error("currentUser accessed before refreshCurrentUser() completed")

  suspend fun refreshCurrentUser(userId: String): NetworkResult<UserModel> {
    val result = getUser(userId)
    if (result is NetworkResult.Success) {
      cachedUser = result.data
    }
    return result
  }
  fun clear(){
    cachedUser = null
  }

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

  suspend fun upload(uriImage: Uri?, bio: String?): NetworkResult<Unit> {
    var imagePart: MultipartBody.Part? = null
    if(uriImage != null) {
      imagePart = try {
        uriToMultipart(uriImage)
      } catch (e: Exception) {
        Log.e(TAG, e.message, e)
        return NetworkResult.Error("Failed to prepare image")
      }
    }

    val metadataPart = bio?.toRequestBody("text/plain".toMediaTypeOrNull())

    return handleRequest(dispatcher) {
      dataSource.uploadUserMedia(imagePart, metadataPart)
    }
  }


  private fun uriToMultipart(uri: Uri): MultipartBody.Part {
    val inputStream = contentResolver.openInputStream(uri)
      ?: throw IOException("Unable to open input stream")

    val bytes = inputStream.use { it.readBytes() }
    val mimeType = contentResolver.getType(uri) ?: "image/*"
    val fileName = "upload_${System.currentTimeMillis()}.jpg"

    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("data", fileName, requestBody)
  }
}