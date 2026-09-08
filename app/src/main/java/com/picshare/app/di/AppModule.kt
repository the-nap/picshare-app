package com.picshare.app.di

import android.content.Context
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.picshare.app.BuildConfig
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.api.auth.AuthInterceptor
import com.picshare.app.api.auth.TokenProvider
import com.picshare.app.context_awareness.sensors.shake.ShakeDetector
import com.picshare.app.data.repository.PostRepository
import com.picshare.app.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  fun provideLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(context)

  @Provides
  @Singleton
  fun provideShakeDetector(@ApplicationContext context: Context): ShakeDetector =
    ShakeDetector(context)

  @Provides
  @Singleton
  fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
    return ImageLoader.Builder(context)
      .crossfade(true)
      .memoryCache {
        MemoryCache.Builder()
          .maxSizePercent(context, 0.25)
          .build()
      }
      .build()
  }

  @Provides
  @Singleton
  fun provideApi(client: OkHttpClient, gson: Gson): PicshareApi {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.API_URL)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
      .create(PicshareApi::class.java)
  }

  @Provides
  @Singleton
  fun getClientWithInterceptors(tokenProvider: TokenProvider): OkHttpClient{
    return OkHttpClient.Builder()
      .addInterceptor(AuthInterceptor({
        runBlocking{
          tokenProvider.getValidAccessToken()
        }
      }))
      .connectTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(120, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .build()
  }

  @Provides
  @Singleton
  fun providePostRepository(api: PicshareApi, gson: Gson, @ApplicationContext context: Context): PostRepository {
    return PostRepository(api, gson, context.contentResolver)
  }

  @Provides
  @Singleton
  fun provideUserRepository(api: PicshareApi, gson: Gson, @ApplicationContext context: Context): UserRepository {
    return UserRepository(api, gson, context.contentResolver)
  }

  @Provides
  @Singleton
  fun provideGson(): Gson{
    return Gson()
  }
}