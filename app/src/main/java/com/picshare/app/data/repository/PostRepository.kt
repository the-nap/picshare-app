package com.picshare.app.data.repository

import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util
import com.picshare.app.data.model.ImageUploadModel
import com.picshare.app.data.model.PostModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostRepository(
  private val dataSource: PicshareApi,
  private val gson: Gson,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

  suspend fun getPost(id: String): Util.NetworkResult<PostModel> {
    return Util.handleRequest(dispatcher) {
      dataSource.getPost(id)
    }
  }

  suspend fun getPosts(key: String, toSearch: String? = null, max: Int = 24, offset: Int = 0): Util.NetworkResult<List<PostModel>> {
    return Util.handleRequest(dispatcher) {
      when (key) {
        "tag" -> dataSource.getPostsByTag(toSearch!!, max, offset)
        "feed" -> dataSource.getPostsByFeed(max, offset)
        "user" -> dataSource.getPostsByUser(toSearch!!, max, offset)
        else -> throw IllegalArgumentException("Unknown key: $key")
      }
    }
  }

  suspend fun upload(media: ImageUploadModel): Util.NetworkResult<Unit> {
    return Util.handleRequest(dispatcher) {
      val filePart = MultipartBody.Part.createFormData(
        name = "data",
        filename = media.file.name,
        body = media.file.asRequestBody("application/octet-stream".toMediaType())
      )

      val metadataJson = gson.toJson(media.post)
      val metadataPart = metadataJson.toRequestBody("application/json; charset=utf-8".toMediaType())

      dataSource.uploadMedia(filePart, metadataPart)
    }
  }

  suspend fun delete(id: String): Util.NetworkResult<Unit> {
    return Util.handleRequest(dispatcher) {
      dataSource.deletePost(id)
    }
  }

  suspend fun like(id: String): Util.NetworkResult<Unit> {
    return Util.handleRequest(dispatcher) {
      dataSource.like(id)
    }
  }

  suspend fun isLiked(id: String): Util.NetworkResult<Boolean> {
    return Util.handleRequest(dispatcher) {
      dataSource.isLiked(id)
    }
  }

}