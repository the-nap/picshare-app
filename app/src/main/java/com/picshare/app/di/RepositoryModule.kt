package com.picshare.app.di

import com.google.gson.Gson
import com.picshare.app.api.network.PicshareApi
import com.picshare.app.auth.AuthRepository
import com.picshare.app.auth.AuthRepositoryImpl
import com.picshare.app.post.PostRepository
import com.picshare.app.ui.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  @Singleton
  abstract fun bindAuthRepository(
    authRepositoryImpl: AuthRepositoryImpl
  ): AuthRepository
}