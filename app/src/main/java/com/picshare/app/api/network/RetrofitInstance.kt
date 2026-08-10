package com.picshare.app.api.network

import com.picshare.app.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

  val api: PicshareApi by lazy {
    Retrofit.Builder()
      .baseUrl(BuildConfig.API_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PicshareApi::class.java)
  }

}