package com.rxmobileteam.pet.presentation.vote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.domain.usecase.GetVoteCatsUseCase
import com.rxmobileteam.pet.domain.usecase.VoteDownCatUseCase
import com.rxmobileteam.pet.domain.usecase.VoteUpCatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class VoteViewModel
  @Inject
  constructor(
    private val getVoteCatsUseCase: GetVoteCatsUseCase,
    private val voteUpCatUseCase: VoteUpCatUseCase,
    private val voteDownCatUseCase: VoteDownCatUseCase,
  ) : ViewModel() {
    private val voteMutableStateFlow = MutableStateFlow<VoteUiState>(VoteUiState.Loading)
    val voteStateFlow = voteMutableStateFlow.asStateFlow()

    private val singleEvent = Channel<VoteSingleEvent>(Channel.UNLIMITED)
    val singleEventFlow: Flow<VoteSingleEvent> get() = singleEvent.receiveAsFlow()

    init {
      viewModelScope.launch {
        voteMutableStateFlow.value = getVoteCatsUseCase(sortBy = SORT_BY, limit = PAGE_SIZE)
          .onFailure { throwable ->
            singleEvent.trySend(
              VoteSingleEvent.GetListError(
                throwable,
              ),
            )
          }
          .fold(
            onSuccess = VoteUiState::Success,
            onFailure = VoteUiState::Error,
          )
      }
    }

    fun voteDown(cat: Cat) {
      val oldList = voteMutableStateFlow.value
      if (oldList is VoteUiState.Success) {
        val voteRequest = oldList.data.filter { it.id != cat.id }
        voteMutableStateFlow.value = VoteUiState.Success(voteRequest)

        // TODO: check logic
        viewModelScope.launch {
          voteDownCatUseCase(cat).fold(
            onSuccess = {},
            onFailure = { Timber.e(it, "Failed to vote down $cat") },
          )
        }
      }
    }

    fun voteUp(cat: Cat) {
      // TODO: check logic
      viewModelScope.launch {
        voteUpCatUseCase(cat).fold(
          onSuccess = {},
          onFailure = { Timber.e(it, "Failed to vote up $cat") },
        )
      }
    }

    companion object {
      private const val SORT_BY = "RANDOM"
      private const val PAGE_SIZE = 30
    }
  }
