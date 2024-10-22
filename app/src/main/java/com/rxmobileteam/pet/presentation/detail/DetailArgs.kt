package com.rxmobileteam.pet.presentation.detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailArgs(
  val id: String,
) : Parcelable
