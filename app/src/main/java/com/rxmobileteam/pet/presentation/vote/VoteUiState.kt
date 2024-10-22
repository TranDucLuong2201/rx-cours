package com.rxmobileteam.pet.presentation.vote

import com.rxmobileteam.pet.domain.model.Cat

sealed interface VoteUiState {
  data object Loading : VoteUiState

  data class Success(val data: List<Cat>) : VoteUiState

  data class Error(val error: Throwable) : VoteUiState
}
