package com.picshare.app.post

import com.google.gson.Gson
import com.picshare.app.api.network.ApiResult
import com.picshare.app.api.network.AppError
import com.picshare.app.api.network.PicshareApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response


class PostRepository(
  private val dataSource: PicshareApi
) {

  suspend fun getPost(id: String): ApiResult<PostModel> {
    return handleRequest {
      dataSource.getPost(id)
    }
  }

  suspend fun getPosts(toSearch: String, requestType: RequestType): ApiResult<List<PostModel>>{
    return handleRequest {
      when(requestType){
        RequestType.TAG -> dataSource.getPostsByTag(toSearch)
        RequestType.FEED -> dataSource.getPostsByFeed()
        RequestType.USER -> dataSource.getPostsByUser(toSearch)
      }
    }
  }

  suspend fun upload(media: ImageUploadModel): ApiResult<Unit>{
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

  suspend fun delete(id: String): ApiResult<Unit>{
    return handleRequest {
      dataSource.deletePost(id)
    }
  }

  suspend fun like(id: String): ApiResult<Unit>{
    return handleRequest {
      dataSource.like(id)
    }
  }

  suspend fun likes(id: String): ApiResult<Boolean>{
    return handleRequest {
      dataSource.isLiked(id)
    }
  }

  suspend fun <T> handleRequest(request: suspend () -> Response<T>): ApiResult<T> {
    return try {
      val response = request()
      if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
          ApiResult.Success(body)
        } else {
          ApiResult.Failure(AppError.EmptyBody)
        }
      } else {
        ApiResult.Failure(
          AppError.Http(
            code = response.code(),
            message = response.errorBody()?.string() ?: response.message()
          )
        )
      }
    } catch (e: java.net.SocketTimeoutException) {
      ApiResult.Failure(AppError.Timeout)
    } catch (e: java.io.IOException) {
      ApiResult.Failure(AppError.Network)
    } catch (t: Throwable) {
      ApiResult.Failure(AppError.Unknown(t))
    }
  }

  enum class RequestType{
    TAG, USER, FEED
  }

}