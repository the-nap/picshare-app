package com.picshare.app.di

import com.picshare.app.BuildConfig
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.post.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
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

  @Provides
  @Singleton
  fun providePostRepository(api: PicshareApi): PostRepository {
    return PostRepository(api)
  }
}