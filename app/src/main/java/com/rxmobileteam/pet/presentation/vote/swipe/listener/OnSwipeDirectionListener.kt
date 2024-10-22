package com.rxmobileteam.pet.presentation.vote.swipe.listener

interface OnSwipeDirectionListener {
  fun onSwiped(position: Int, direction: Int)
  fun onChangeHorizontalDrag(direction: Int, percent: Float)
}
