package com.picshare.app.di

import com.picshare.app.BuildConfig
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.auth.AuthInterceptor
import com.picshare.app.post.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideApi(): PicshareApi {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.API_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PicshareApi::class.java)
  }

  @Singleton
  private fun getClientWithInterceptors(): OkHttpClient{
    return OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor(/*TODO("add token provider")*/))
      .build()
  }

  @Provides
  @Singleton
  fun providePostRepository(api: PicshareApi): PostRepository {
    return PostRepository(api)
  }
}