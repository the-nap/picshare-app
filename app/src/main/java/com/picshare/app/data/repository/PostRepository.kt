package com.picshare.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util.NetworkResult
import com.picshare.app.api.network.Util.handleRequest
import com.picshare.app.data.model.PostModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class PostRepository(
  private val dataSource: PicshareApi,
  private val gson: Gson,
  private val contentResolver: ContentResolver,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

  private val TAG = this.javaClass.simpleName

  suspend fun getPost(id: String): NetworkResult<PostModel> {
    return handleRequest(dispatcher) {
      dataSource.getPost(id)
    }
  }

  suspend fun getPosts(key: String, toSearch: String? = null, max: Int = 24, offset: Int = 0): NetworkResult<List<PostModel>> {
    return handleRequest(dispatcher) {
      when (key) {
        "tag" -> dataSource.getPostsByTag(toSearch!!, max, offset)
        "feed" -> dataSource.getPostsByFeed(max, offset)
        "user" -> dataSource.getPostsByUser(toSearch!!, max, offset)
        else -> throw IllegalArgumentException("Unknown key: $key")
      }
    }
  }

  suspend fun upload(uriImage: Uri, postModel: PostModel): NetworkResult<Unit> {
    val imagePart: MultipartBody.Part = try {
        uriToMultipart(uriImage)
      } catch (e: Exception) {
        Log.e(TAG, e.message, e)
        return NetworkResult.Error("Failed to prepare image")
      }

      val metadataJson = gson.toJson(postModel)
      val metadataPart = metadataJson.toRequestBody("application/json; charset=utf-8".toMediaType())

      return handleRequest(dispatcher){
        dataSource.uploadMedia(imagePart, metadataPart)
      }
    }

  suspend fun delete(id: String): NetworkResult<Unit> {
    return handleRequest(dispatcher) {
      dataSource.deletePost(id)
    }
  }

  suspend fun like(id: String): NetworkResult<Unit> {
    return handleRequest(dispatcher) {
      dataSource.like(id)
    }
  }

  suspend fun isLiked(id: String): NetworkResult<Boolean> {
    return handleRequest(dispatcher) {
      dataSource.isLiked(id)
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