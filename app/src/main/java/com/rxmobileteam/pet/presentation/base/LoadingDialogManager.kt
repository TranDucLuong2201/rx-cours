package com.rxmobileteam.pet.presentation.base

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@MainThread
@FragmentScoped
class LoadingDialogManager @Inject constructor(
  private val host: Fragment,
) {
  private var _loadingDialog: LoadingDialog? = null

  init {
    host.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onCreate(owner: LifecycleOwner) {
        host.viewLifecycleOwnerLiveData.observe(host) { viewLifecycleOwner: LifecycleOwner? ->
          viewLifecycleOwner ?: return@observe kotlin.run {
            _loadingDialog?.dismiss()
            _loadingDialog = null
          }

          viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
              _loadingDialog?.dismiss()
              _loadingDialog = null
            }
          })
        }
      }

      override fun onDestroy(owner: LifecycleOwner) {
        _loadingDialog?.dismiss()
        _loadingDialog = null
      }
    })
  }

  fun showLoading(show: Boolean) {
    val dialog = _loadingDialog
      ?: LoadingDialog(host.requireContext()).also { _loadingDialog = it }

    if (show) {
      dialog.show()
    } else {
      dialog.dismiss()
    }
  }
}
