package com.rxmobileteam.pet.presentation.vote.swipe.manager

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.rxmobileteam.pet.presentation.vote.swipe.listener.OnEventDragListener
import com.rxmobileteam.pet.presentation.vote.swipe.listener.OnSwipeDirectionListener
import kotlin.math.abs
import kotlin.math.sqrt

class PetSwipeLayoutManager(private val dragEventListener: OnEventDragListener) :
  RecyclerView.LayoutManager() {
  private var onSwipeDirectionListener: OnSwipeDirectionListener? = null
  private var startXPosition: Float? = null
  private var startYPosition: Float? = null
  private var topPosition = 0

  private var swipeFlag = SWIPE_LEFT or SWIPE_RIGHT or SWIPE_TOP or SWIPE_BOTTOM
  private val preloadCount = 3
  private val scaleGap = 0.1f
  private val dragThreshold = .2f
  private var defaultElevation = 0f
  private val restoreScaleAnimationDuration = 200L
  private val autoDraggingAnimationDuration = 200L

  override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
    return RecyclerView.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT,
    )
  }

  override fun onLayoutChildren(
    recycler: RecyclerView.Recycler,
    state: RecyclerView.State,
  ) {
    detachAndScrapAttachedViews(recycler)
    if (itemCount < 1) return

    val startPosition = (itemCount - 1).coerceAtMost(topPosition + preloadCount - 1)

    for (position in startPosition downTo topPosition) {
      val isTopView = position == topPosition
      val drawPosition = position - topPosition
      val view = recycler.getViewForPosition(position)

      changeDefaultElevation(ViewCompat.getElevation(view))

      if (isTopView) view.postDelayed({ setTopViewOnTouchListener(view) }, 100)

      setElevation(
        view,
        if (isTopView) {
          defaultElevation
        } else {
          defaultElevation - 1f * drawPosition
        },
      )

      measureChildWithMargins(view, 0, 0)

      val params = view.layoutParams as RecyclerView.LayoutParams

      layoutDecorated(
        view,
        params.leftMargin + paddingLeft,
        params.topMargin + paddingTop,
        getDecoratedMeasuredWidth(view) + params.leftMargin + paddingLeft,
        getDecoratedMeasuredHeight(view) + params.topMargin + paddingTop,
      )

      if (drawPosition > 0) {
        view.scaleX = validateScale(1 - scaleGap * drawPosition)
        view.scaleY = validateScale(1 - scaleGap * drawPosition)
      } else {
        view.scaleX = validateScale(1f)
        view.scaleY = validateScale(1f)
      }

      if (startXPosition == null || startYPosition == null) {
        initStartPosition(view.x, view.y)
      }

      addView(view)
    }
  }

  /**
   * Change the child view's scale value by dragging the parent view.
   */
  fun changeChildViewScale(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    dX: Float,
    dY: Float,
  ) {
    val swipeValue = sqrt(dX * dX + dY * dY.toDouble())
    val fraction = 1.0.coerceAtMost(swipeValue / getThreshold(viewHolder))
    val childCount = recyclerView.childCount

    for (i in 0 until childCount) {
      val child = recyclerView.getChildAt(i)
      val level: Int = childCount - i - 1
      if (level > 0) {
        val scale = 0f.coerceAtLeast(
          1f.coerceAtMost((1 - scaleGap * level + fraction * scaleGap).toFloat()),
        )
        child.scaleX = scale
        if (level < preloadCount - 1) {
          child.scaleY = scale
        }
      }
    }
  }

  fun getCurrentPosition(): Int {
    return topPosition
  }

  fun nextView() {
    if (topPosition + 1 > itemCount - 1) topPosition = 0 else topPosition++
    requestLayout()
  }

  fun previousView() {
    if (topPosition - 1 < 0) topPosition = itemCount - 1 else topPosition--
    requestLayout()
  }

  fun resetPosition() {
    topPosition = 0
    requestLayout()
  }

  fun setOnSwipeListener(onSwipeDirectionListener: OnSwipeDirectionListener) {
    this.onSwipeDirectionListener = onSwipeDirectionListener
  }

  fun changeDragPercent(
    recyclerView: RecyclerView,
    dX: Float,
    dY: Float,
  ) {
    val swipeWidthPercent = 1f.coerceAtMost(dX / recyclerView.measuredWidth)
    val swipeHeightPercent = 1f.coerceAtMost(dY / recyclerView.measuredHeight)

    if (isEnableDirection(judgeLeftOrRight(swipeWidthPercent))) {
      onSwipeDirectionListener?.onChangeHorizontalDrag(
        judgeLeftOrRight(swipeWidthPercent),
        abs(swipeWidthPercent),
      )
    }
    if (isEnableDirection(judgeTopOrBottom(swipeHeightPercent))) {
      onSwipeDirectionListener?.onChangeHorizontalDrag(
        judgeTopOrBottom(swipeHeightPercent),
        abs(swipeHeightPercent),
      )
    }
  }

  fun performSwipe(
    recyclerView: RecyclerView,
    direction: Int,
  ) {
    val topView = getTopView(recyclerView)
    if (isEnableDirection(direction) && topView != null) {
      onSwiped(topView, getSecondView(recyclerView), direction)
    }
  }

  fun onDragEnded(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    dX: Float,
    dY: Float,
  ) {
    clearViewTouchListener(viewHolder.itemView)

    val swipeWidthPercent = 1f.coerceAtMost(dX / recyclerView.measuredWidth)
    val swipeHeightPercent = 1f.coerceAtMost(dY / recyclerView.measuredHeight)

    getCandidateDirection(
      swipeWidthPercent,
      swipeHeightPercent,
    ).firstOrNull { isEnableDirection(it) }
      ?.let {
        onSwiped(
          viewHolder.itemView,
          getSecondView(recyclerView),
          it,
        )
      }
      ?: run {
        transitionAnimation(
          viewHolder.itemView,
          startXPosition ?: 0f,
          startYPosition ?: 0f,
          restoreScaleAnimationDuration,
          {
            dragEventListener.onDrag(it, it.x, it.y)
          },
          {
            postOnAnimation { setTopViewOnTouchListener(viewHolder.itemView) }
          },
        )
      }
  }

  private fun initStartPosition(
    x: Float,
    y: Float,
  ) {
    startXPosition = x
    startYPosition = y
  }

  private fun changeDefaultElevation(elevation: Float) {
    defaultElevation = defaultElevation.coerceAtLeast(elevation)
  }

  private fun setElevation(
    view: View,
    elevation: Float,
  ) {
    ViewCompat.setElevation(view, elevation)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setTopViewOnTouchListener(topView: View) {
    var rawX = 0f
    var rawY = 0f
    val pressedStateDuration = android.view.ViewConfiguration.getPressedStateDuration().toLong()
    var lastMoveOrDown = 0L
    topView.setOnTouchListener { v, event ->
      if (v.scaleX != 1f || v.scaleY != 1f) return@setOnTouchListener true

      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          rawX = v.x - event.rawX
          rawY = v.y - event.rawY
          if (lastMoveOrDown == 0L) {
            lastMoveOrDown = SystemClock.uptimeMillis()
          }
          true
        }

        MotionEvent.ACTION_UP -> {
          dragEventListener.onDragEnded(
            v,
            rawX + event.rawX,
            rawY + event.rawY,
          )
          if (lastMoveOrDown > 0L && SystemClock.uptimeMillis() - lastMoveOrDown <= pressedStateDuration) {
            dragEventListener.onClick(v)
          }
          lastMoveOrDown = 0L
          true
        }

        MotionEvent.ACTION_MOVE -> {
          dragEventListener.onDrag(v, rawX + event.rawX, rawY + event.rawY)
          transitionAnimation(v, rawX + event.rawX, rawY + event.rawY) {
          }
          if (lastMoveOrDown == 0L) {
            lastMoveOrDown = SystemClock.uptimeMillis()
          }
          true
        }

        else -> {
          lastMoveOrDown = 0L
          false
        }
      }
    }
  }

  private fun isSwiped(swipedPercent: Float): Boolean {
    return swipedPercent > dragThreshold
  }

  private fun getThreshold(viewHolder: RecyclerView.ViewHolder): Float {
    return viewHolder.itemView.width * 0.9f
  }

  private fun judgeTopOrBottom(percent: Float): Int =
    if (percent > 0) {
      SWIPE_BOTTOM
    } else {
      SWIPE_TOP
    }

  private fun judgeLeftOrRight(percent: Float): Int =
    if (percent > 0) {
      SWIPE_RIGHT
    } else {
      SWIPE_LEFT
    }

  private fun swipedAnimation(
    x: Float,
    y: Float,
    swipeDirect: Int,
    swipedView: View,
  ) {
    transitionAnimation(swipedView, x, y, autoDraggingAnimationDuration) {
      resetViewPosition(swipedView)

      when (swipeDirect) {
        SWIPE_TOP -> {
          previousView()
        }

        SWIPE_BOTTOM -> {
          nextView()
        }

        SWIPE_LEFT -> {
          nextView()
        }

        SWIPE_RIGHT -> {
          previousView()
        }
      }
      val currentPosition = topPosition
      onSwipeDirectionListener?.onSwiped(currentPosition, swipeDirect)
    }
  }

  private fun onSwiped(
    swipedView: View,
    secondView: View?,
    direction: Int,
  ) {
    if (secondView != null) {
      restoreScaleAnimation(secondView, autoDraggingAnimationDuration)
    }

    val value = 3f

    when (direction) {
      SWIPE_TOP -> {
        swipedAnimation(
          swipedView.x * value,
          swipedView.y - swipedView.height,
          direction,
          swipedView,
        )
      }

      SWIPE_BOTTOM -> {
        swipedAnimation(
          swipedView.x * value,
          swipedView.y + swipedView.height,
          direction,
          swipedView,
        )
      }

      SWIPE_LEFT -> {
        swipedAnimation(
          swipedView.x - swipedView.width,
          swipedView.y * value,
          direction,
          swipedView,
        )
      }

      SWIPE_RIGHT -> {
        swipedAnimation(
          swipedView.x + swipedView.width,
          swipedView.y * value,
          direction,
          swipedView,
        )
      }
    }
  }

  private fun validateScale(value: Float): Float {
    return 0f.coerceAtLeast(1f.coerceAtMost(value))
  }

  private fun transitionAnimation(
    view: View,
    x: Float,
    y: Float,
    duration: Long = 0,
    updateListener: ((View) -> Unit)? = null,
    endAction: (() -> Unit),
  ) {
    ViewCompat.animate(view)
      .x(x)
      .y(y)
      .setUpdateListener(updateListener)
      .setDuration(duration)
      .withEndAction(endAction)
      .start()
  }

  private fun restoreScaleAnimation(
    view: View,
    duration: Long = 0,
  ) {
    view.animate()
      .scaleX(1f)
      .scaleY(1f)
      .setDuration(duration)
      .start()
  }

  private fun resetViewPosition(view: View) {
    view.x = startXPosition ?: 0f
    view.y = startYPosition ?: 0f
  }

  private fun clearViewTouchListener(view: View) {
    view.setOnTouchListener(null)
  }

  private fun getSecondView(recyclerView: RecyclerView): View? {
    if (recyclerView.childCount < 2) return null
    return recyclerView.getChildAt(childCount - 2)
  }

  private fun getTopView(recyclerView: RecyclerView): View? {
    if (recyclerView.childCount == 0) return null
    return recyclerView.getChildAt(childCount - 1)
  }

  private fun getCandidateDirection(
    widthPercent: Float,
    heightPercent: Float,
  ): List<Int> {
    val candidates = mutableListOf<Pair<Float, Int>>()

    if (isSwiped(abs(widthPercent))) {
      candidates.add(widthPercent to judgeLeftOrRight(widthPercent))
    }

    if (isSwiped(abs(heightPercent))) {
      candidates.add(heightPercent to judgeTopOrBottom(heightPercent))
    }

    return candidates
      .sortedByDescending { abs(it.first) }
      .map { it.second }
  }

  private fun isEnableDirection(direction: Int) = (swipeFlag and direction) == direction

  companion object {
    const val SWIPE_LEFT = 1
    const val SWIPE_RIGHT = 1 shl 1
    const val SWIPE_TOP = 1 shl 2
    const val SWIPE_BOTTOM = 1 shl 3
  }
}
