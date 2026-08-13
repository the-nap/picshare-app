package com.picshare.app.post

import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.network.Util.handleRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostRepository(
  private val dataSource: PicshareApi,
  private val gson: Gson
) {

  suspend fun getPost(id: String): PostModel {
    return handleRequest {
      dataSource.getPost(id)
    }
  }

  suspend fun getPosts(key: String, toSearch: String? = null, max: Int = 24, offset: Int = 0): List<PostModel>{
    return handleRequest {
      when(key){
        "tag" -> dataSource.getPostsByTag(toSearch!!, max, offset)
        "feed" -> dataSource.getPostsByFeed(max, offset)
        "user" -> dataSource.getPostsByUser(toSearch!!, max, offset)
        else -> throw IllegalArgumentException("Unknown key: $key")
      }
    }
  }

  suspend fun upload(media: ImageUploadModel){
    val filePart = MultipartBody.Part.createFormData(
      name = "data",
      filename = media.file.name,
      body = media.file.asRequestBody("application/octet-stream".toMediaType())
    )

    val metadataJson = gson.toJson(media.post)
    val metadataPart = metadataJson.toRequestBody("application/json; charset=utf-8".toMediaType())
    return handleRequest {
      dataSource.uploadMedia(filePart, metadataPart)
    }
  }

  suspend fun delete(id: String){
    return handleRequest {
      dataSource.deletePost(id)
    }
  }

  suspend fun like(id: String){
    return handleRequest {
      dataSource.like(id)
    }
  }

  suspend fun isLiked(id: String): Boolean{
    return handleRequest {
      dataSource.isLiked(id)
    }
  }

}