package com.picshare.app.post

import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response


class PostRepository(
  private val dataSource: PicshareApi
) {

  suspend fun getPost(id: String): PostModel {
    return handleRequest {
      dataSource.getPost(id)
    }
  }

  suspend fun getPosts(toSearch: String, key: String): List<PostModel>{
    return handleRequest {
      when(key){
        "tag" -> dataSource.getPostsByTag(toSearch)
        "feed" -> dataSource.getPostsByFeed()
        "user" -> dataSource.getPostsByUser(toSearch)
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

    val metadataJson = Gson().toJson(media.post)
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

  suspend fun likes(id: String): Boolean{
    return handleRequest {
      dataSource.isLiked(id)
    }
  }

  suspend fun <T> handleRequest(request: suspend () -> T): T {
    return request()
  }
}