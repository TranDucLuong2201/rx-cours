package com.rxmobileteam.pet.initializer

import android.content.Context
import androidx.startup.Initializer
import com.rxmobileteam.pet.BuildConfig
import com.rxmobileteam.pet.crashlytics.CrashlyticsLoggerTree
import timber.log.Timber

@Suppress("unused")
class TimberInitializer : Initializer<Unit> {
  override fun create(context: Context) {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    } else {
      Timber.plant(CrashlyticsLoggerTree())
    }
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

