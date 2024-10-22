package com.rxmobileteam.pet.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.domain.usecase.ObserveFavoriteCatsUseCase
import com.rxmobileteam.pet.domain.usecase.VoteDownCatUseCase
import com.rxmobileteam.pet.utils.WHILE_UI_SUBSCRIBED
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

internal sealed interface FavoriteUiState {
  data object Loading : FavoriteUiState

  data class Success(val cats: List<Cat>) : FavoriteUiState

  data class Error(val error: Throwable) : FavoriteUiState
}

internal sealed interface FavoriteSingleEvent {
  data class VoteDownSuccess(
    val cat: Cat,
  ) : FavoriteSingleEvent

  data class VoteDownError(
    val cat: Cat,
    val throwable: Throwable,
  ) : FavoriteSingleEvent
}

@HiltViewModel
internal class FavoriteViewModel
  @Inject
  constructor(
    observeFavoriteCatsUseCase: ObserveFavoriteCatsUseCase,
    private val voteDownCatUseCase: VoteDownCatUseCase,
  ) : ViewModel() {
    private val _singleEventChannel = Channel<FavoriteSingleEvent>(Channel.UNLIMITED)
      .apply { addCloseable(::close) }

    /**
     * This flow is used to trigger the loading of the favorite cats.
     * A true value means that is a retry.
     * A false value means that is the first time loading.
     */
    private val _loadFlow = MutableSharedFlow<Boolean>(
      extraBufferCapacity = 1,
      onBufferOverflow = DROP_OLDEST,
    )

    internal val singleEventFlow: Flow<FavoriteSingleEvent> = _singleEventChannel.receiveAsFlow()

    internal val uiStateFlow: StateFlow<FavoriteUiState> = _loadFlow
      .onStart { emit(false) }
      .flatMapLatest { isRetry ->
        observeFavoriteCatsUseCase()
          .map { result ->
            result
              .onFailure { Timber.e(it, "Failed to #observeFavoriteCatsUseCase") }
              .fold(
                onSuccess = FavoriteUiState::Success,
                onFailure = FavoriteUiState::Error,
              )
          }
          .onStart { if (isRetry) emit(FavoriteUiState.Loading) }
      }
      .stateIn(
        scope = viewModelScope,
        started = WHILE_UI_SUBSCRIBED,
        initialValue = FavoriteUiState.Loading,
      )

    internal fun voteDownCat(cat: Cat) {
      viewModelScope.launch {
        voteDownCatUseCase(cat)
          .onFailure { Timber.e(it, "Failed to #voteDownCatUseCase($cat)") }
          .fold(
            onSuccess = { FavoriteSingleEvent.VoteDownSuccess(cat) },
            onFailure = { FavoriteSingleEvent.VoteDownError(cat, it) },
          )
          .also(_singleEventChannel::trySend)
      }
    }

    internal fun retry() {
      viewModelScope.launch { _loadFlow.emit(true) }
    }
  }
