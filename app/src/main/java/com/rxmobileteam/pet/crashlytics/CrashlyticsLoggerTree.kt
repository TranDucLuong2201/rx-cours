package com.rxmobileteam.pet.crashlytics

import android.util.Log
import timber.log.Timber

class CrashlyticsLoggerTree : Timber.Tree() {
  override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.INFO

  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    t ?: return

    val priority = when (priority) {
      Log.INFO -> "Log.INFO"
      Log.WARN -> "Log.WARN"
      Log.ERROR -> "Log.ERROR"
      Log.ASSERT -> "Log.ASSERT"
      else -> "Log.$priority"
    }
    val tag = tag?.ifEmpty { "<empty>" } ?: "<null>"
    val message = message.ifEmpty { "<empty>" }
    val throwableMessage = t.message ?: "<null>"
    // TODO: Log all infos to Firebase Crashlytics
  }
}
