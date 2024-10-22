package com.rxmobileteam.pet.presentation.vote.swipe.manager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rxmobileteam.pet.presentation.vote.swipe.listener.OnEventDragListener
import com.rxmobileteam.pet.presentation.vote.swipe.listener.OnSwipeDirectionListener

class PetSwipeRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {
  private var onSwipeDirectionListener: OnSwipeDirectionListener? = null
  private val catSwipeLayoutManager: PetSwipeLayoutManager
  private var onItemClickListener: OnClickListener? = null

  init {
    catSwipeLayoutManager = PetSwipeLayoutManager(getInternalDragListener())
    layoutManager = catSwipeLayoutManager
  }

  fun setOnSwipeListener(onSwipeDirectionListener: OnSwipeDirectionListener) {
    this.onSwipeDirectionListener = onSwipeDirectionListener
    catSwipeLayoutManager.setOnSwipeListener(onSwipeDirectionListener)
  }

  fun setOnItemClickListener(listener: OnClickListener?) {
    onItemClickListener = listener
  }

  fun nextView() {
    catSwipeLayoutManager.nextView()
  }

  fun previousView() {
    catSwipeLayoutManager.previousView()
  }

  fun resetPosition() {
    catSwipeLayoutManager.resetPosition()
  }

  fun performSwipe(direction: Int) {
    catSwipeLayoutManager.performSwipe(this, direction)
  }

  private fun getInternalDragListener() =
    object : OnEventDragListener {
      override fun onDrag(
        v: View,
        x: Float,
        y: Float,
      ) {
        catSwipeLayoutManager.run {
          changeChildViewScale(this@PetSwipeRecyclerView, getChildViewHolder(v), x, y)
          changeDragPercent(this@PetSwipeRecyclerView, x, y)
        }
      }

      override fun onDragEnded(
        v: View,
        x: Float,
        y: Float,
      ) {
        catSwipeLayoutManager.onDragEnded(this@PetSwipeRecyclerView, getChildViewHolder(v), x, y)
      }

      override fun onClick(v: View) {
        onItemClickListener?.onClick(v)
      }
    }

  fun getCurrentPosition(): Int {
    return catSwipeLayoutManager.getCurrentPosition()
  }
}
