package com.rxmobileteam.pet.presentation.vote.swipe.listener

import android.view.View

interface OnEventDragListener {
  fun onDrag(
    v: View,
    x: Float,
    y: Float,
  )

  fun onDragEnded(
    v: View,
    x: Float,
    y: Float,
  )

  fun onClick(v: View)
}
