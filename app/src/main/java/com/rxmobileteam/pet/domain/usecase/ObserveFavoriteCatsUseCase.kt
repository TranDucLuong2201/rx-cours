package com.rxmobileteam.pet.domain.usecase

import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.domain.repository.FavoriteCatRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteCatsUseCase
  @Inject
  constructor(
    private val favoriteCatRepository: FavoriteCatRepository,
  ) {
    operator fun invoke(): Flow<Result<List<Cat>>> = favoriteCatRepository.observeFavoriteCats()
  }
