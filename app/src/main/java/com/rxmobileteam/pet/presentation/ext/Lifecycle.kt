package com.rxmobileteam.pet.presentation.ext

import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

fun Fragment.launchAndRepeatStarted(
  launchBlock: suspend () -> Unit,
  vararg launchBlocks: suspend () -> Unit,
  doAfterLaunch: (() -> Unit)? = null
) {
  viewLifecycleOwner.lifecycleScope.launch {
    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      launch { launchBlock() }
      launchBlocks.forEach { launch { it() } }
      doAfterLaunch?.invoke()
    }
  }
}

fun ComponentActivity.launchAndRepeatStarted(
  launchBlock: suspend () -> Unit,
  vararg launchBlocks: suspend () -> Unit,
  doAfterLaunch: (() -> Unit)? = null
) {
  lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
      launch { launchBlock() }
      launchBlocks.forEach { launch { it() } }
      doAfterLaunch?.invoke()
    }
  }
}
