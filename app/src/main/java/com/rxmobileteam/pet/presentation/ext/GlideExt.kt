package com.rxmobileteam.pet.presentation.ext

import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.rxmobileteam.pet.R

fun ImageView.loadImageFromUrlWithLoading(url: String?, resize: Int? = null) {
  val circularProgressDrawable = CircularProgressDrawable(context)
  circularProgressDrawable.strokeWidth = 5f
  circularProgressDrawable.centerRadius = 30f
  circularProgressDrawable.start()
  Glide.with(context)
    .load(url)
    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
    .placeholder(circularProgressDrawable)
    .error(R.drawable.ic_placeholder_portrait)
    .into(this)
}
