package com.picshare.app.di

import com.picshare.app.BuildConfig
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.auth.AuthInterceptor
import com.picshare.app.auth.AuthRepository
import com.picshare.app.post.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideApi(client: OkHttpClient): PicshareApi {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.API_URL)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PicshareApi::class.java)
  }

  @Provides
  @Singleton
  fun getClientWithInterceptors(authRepository: AuthRepository): OkHttpClient{
    return OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor({
        runBlocking{
          authRepository.getValidAccessToken()
        }
      }))
      .build()
  }

  @Provides
  @Singleton
  fun providePostRepository(api: PicshareApi): PostRepository {
    return PostRepository(api)
  }
}