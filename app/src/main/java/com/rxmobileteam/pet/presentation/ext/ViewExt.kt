@file:Suppress("NOTHING_TO_INLINE")

package com.rxmobileteam.pet.presentation.ext

import android.view.View
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun View.visible() {
  if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

inline fun View.gone() {
  if (visibility != View.GONE) visibility = View.GONE
}

inline fun View.invisible() {
  if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

inline fun View.visibleIf(
  condition: Boolean,
  gone: Boolean = true,
) = if (condition) {
  visible()
} else {
  if (gone) gone() else invisible()
}

@OptIn(ExperimentalContracts::class)
inline fun View.visibleIf(
  gone: Boolean = true,
  conditionLambda: () -> Boolean,
) {
  contract { callsInPlace(conditionLambda, InvocationKind.EXACTLY_ONCE) }
  visibleIf(condition = conditionLambda(), gone = gone)
}
