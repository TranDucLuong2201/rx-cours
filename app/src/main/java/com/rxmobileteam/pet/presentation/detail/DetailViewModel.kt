package com.rxmobileteam.pet.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
) : ViewModel() {
  internal val args = DetailFragmentArgs.fromSavedStateHandle(savedStateHandle).args

  init {
    // TODO: Use args.id to load detail
  }
}
