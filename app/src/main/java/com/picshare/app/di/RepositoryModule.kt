package com.picshare.app.di

import com.picshare.app.api.auth.AuthRepository
import com.picshare.app.api.auth.AuthRepositoryImpl
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