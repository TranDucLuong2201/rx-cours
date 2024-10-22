package com.rxmobileteam.pet.presentation.ext

import androidx.navigation.NavController
import timber.log.Timber

/**
 * Returns `true` if [NavController.graph] is set.
 * This is useful for determining if the [NavController] has a graph.
 */
val NavController.isGraphInitialized: Boolean
  get() = try {
    graph
    true
  } catch (_: IllegalStateException) {
    false
  }

/**
 * Returns `true` if [NavController.graph] is not set.
 */
val NavController.isGraphNotInitialized: Boolean get() = !isGraphInitialized

/**
 * Performs a navigation on the [NavController] using the provided [block],
 * catching any [IllegalArgumentException] which usually happens when users trigger (e.g. click)
 * navigation multiple times very quickly on slower devices.
 *
 * For more context, see [stackoverflow](https://stackoverflow.com/questions/51060762/illegalargumentexception-navigation-destination-xxx-is-unknown-to-this-navcontr).
 */
inline fun NavController.safeNavigate(block: NavController.() -> Unit) {
  try {
    this.block()
  } catch (e: IllegalArgumentException) {
    Timber.e(e, "Handled navigation destination not found issue gracefully.")
  }
}
