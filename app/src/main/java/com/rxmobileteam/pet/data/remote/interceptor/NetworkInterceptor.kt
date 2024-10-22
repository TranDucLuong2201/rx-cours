package com.rxmobileteam.pet.data.remote.interceptor

import com.rxmobileteam.pet.data.di.ApiKeyPetsQualifier
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor @Inject constructor(
  @ApiKeyPetsQualifier val apiKey: String
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response =
    chain
      .request()
      .newBuilder()
      .addHeader("x-api-key", apiKey)
      .build()
      .let(chain::proceed)
}
