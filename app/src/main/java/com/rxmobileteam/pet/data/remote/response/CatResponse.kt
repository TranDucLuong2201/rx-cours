package com.rxmobileteam.pet.data.remote.response

import com.squareup.moshi.Json

data class CatResponse(
  @Json(name = "id") val id: String,
  @Json(name = "url") val urlImage: String,
  @Json(name = "width") val width: Int,
  @Json(name = "height") val height: Int,
)
