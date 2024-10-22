package com.rxmobileteam.pet.presentation.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.rxmobileteam.pet.R
import com.rxmobileteam.pet.databinding.LoadingDialogBinding
import kotlin.LazyThreadSafetyMode.NONE

class LoadingDialog(context: Context) : Dialog(context) {
  private val binding by lazy(NONE) { LoadingDialogBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    window?.setBackgroundDrawableResource(R.color.transparent)
    setCancelable(false)
  }
}
