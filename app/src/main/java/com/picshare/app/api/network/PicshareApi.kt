package com.picshare.app.api.network

import com.picshare.app.post.PostModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PicshareApi {

  @GET("/post/{id}")
  suspend fun getPost(@Path("id") id: String): PostModel

  @DELETE("/post/{id}/delete")
  suspend fun deletePost(@Path("id") id: String): Unit

  @GET("/feed")
  suspend fun getPostsByFeed(): List<PostModel>

  @GET("/post/user/{id}")
  suspend fun getPostsByUser(@Path("id") id: String): List<PostModel>

  @GET("/post/tags/{tag}")
  suspend fun getPostsByTag(@Path("tag") tag: String): List<PostModel>

  @Multipart
  @POST("post/upload")
  suspend fun uploadMedia(@Part data: MultipartBody.Part, @Part("metadata") metadata: RequestBody)

  @POST("/post/{id}/like")
  suspend fun like(@Path("id") id: String)

  @GET("post/{id}/likes")
  suspend fun isLiked(@Path("id") id: String): Boolean

}