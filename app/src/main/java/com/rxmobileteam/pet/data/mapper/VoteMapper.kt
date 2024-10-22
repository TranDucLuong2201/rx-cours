package com.rxmobileteam.pet.data.mapper

import com.rxmobileteam.pet.data.remote.response.CatResponse
import com.rxmobileteam.pet.domain.model.Cat

fun CatResponse.toCatDomain() =
  Cat(
    id = id,
    url = urlImage,
    width = width,
    height = height,
  )
