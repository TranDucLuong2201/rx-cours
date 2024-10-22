package com.rxmobileteam.pet.domain.usecase

import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.domain.repository.FavoriteCatRepository
import javax.inject.Inject

class VoteUpCatUseCase
  @Inject
  constructor(
    private val favoriteCatRepository: FavoriteCatRepository,
  ) {
    suspend operator fun invoke(cat: Cat): Result<Unit> = favoriteCatRepository.voteUp(cat)
  }
