package com.rxmobileteam.pet.presentation.vote

sealed interface VoteSingleEvent {
  data object VoteError : VoteSingleEvent

  data class GetListError(
    val throwable: Throwable,
  ) : VoteSingleEvent
}
