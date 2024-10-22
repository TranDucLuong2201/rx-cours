package com.rxmobileteam.pet.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
  companion object {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
      Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
  }
}
