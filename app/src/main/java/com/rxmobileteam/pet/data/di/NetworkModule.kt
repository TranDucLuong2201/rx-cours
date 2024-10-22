package com.rxmobileteam.pet.data.di

import com.rxmobileteam.pet.BuildConfig
import com.rxmobileteam.pet.data.remote.PetService
import com.rxmobileteam.pet.data.remote.interceptor.NetworkInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrlPetsQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiKeyPetsQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiRequest

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @BaseUrlPetsQualifier
  fun provideBaseUrlPets(): String = BuildConfig.API_DOMAIN

  @Provides
  @ApiKeyPetsQualifier
  fun provideApiKeyClient(): String = BuildConfig.API_KEY


  @Provides
  fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
      HttpLoggingInterceptor.Level.BODY
    } else {
      HttpLoggingInterceptor.Level.NONE
    }
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(
    authorizationInterceptor: NetworkInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .addNetworkInterceptor(httpLoggingInterceptor)
    .addInterceptor(authorizationInterceptor)
    .build()

  @Provides
  @Singleton
  @ApiRequest
  fun provideRetrofit(
    moshi: Moshi,
    okHttpClient: OkHttpClient,
    @BaseUrlPetsQualifier baseUrl: String
  ): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

  @Provides
  fun providePetsService(@ApiRequest retrofit: Retrofit): PetService =
    retrofit.create()
}
