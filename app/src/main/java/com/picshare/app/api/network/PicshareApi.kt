package com.picshare.app.api.network

import com.picshare.app.post.PostModel
import com.picshare.app.user.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PicshareApi {

  @GET("/post/{id}")
  suspend fun getPost(@Path("id") id: String): PostModel

  @DELETE("/post/{id}/delete")
  suspend fun deletePost(@Path("id") id: String)

  @GET("/feed")
  suspend fun getPostsByFeed(@Query("max") max: Int, @Query("offset") offset: Int): List<PostModel>

  @GET("/post/user/{id}")
  suspend fun getPostsByUser(@Path("id") id: String, @Query("max") max: Int, @Query("offset")offset: Int): List<PostModel>

  @GET("/post/tags/{tag}")
  suspend fun getPostsByTag(@Path("tag") tag: String, @Query("max") max: Int, @Query("offset")offset: Int): List<PostModel>

  @Multipart
  @POST("post/upload")
  suspend fun uploadMedia(@Part data: MultipartBody.Part, @Part("metadata") metadata: RequestBody)

  @POST("/post/{id}/like")
  suspend fun like(@Path("id") id: String)

  @GET("post/{id}/likes")
  suspend fun isLiked(@Path("id") id: String): Boolean

  @GET("/user/{id}")
  suspend fun getUser(@Path("id") id: String): UserModel

  @GET("/user/name/{username}")
  suspend fun getByUsername(@Path("username") username: String): UserModel

  @GET("/user/follows")
  suspend fun follows(@Query("followed") user: String): Boolean

  @POST("/user/follow")
  suspend fun follow(@Body toFollow: String): String

  @POST("/user/unfollow")
  suspend fun unfollow(@Body toFollow: String): String

  @GET("/user/contains")
  suspend fun contains(@Query("toSearch") toSearch: String, @Query("offset") offset: Number, @Query("max") max: Number)

  @Multipart
  @POST("/user/upload")
  suspend fun uploadUserMedia(@Part data: MultipartBody.Part, @Part("metadata") metadata: RequestBody)

  @DELETE("/user")
  suspend fun deleteUser()
}