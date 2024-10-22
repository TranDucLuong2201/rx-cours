package com.rxmobileteam.pet.domain.usecase

import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.domain.repository.VoteCatRepository
import javax.inject.Inject

class GetVoteCatsUseCase
  @Inject
  constructor(private val voteRepository: VoteCatRepository) {
    suspend operator fun invoke(
      sortBy: String,
      limit: Int,
    ): Result<List<Cat>> =
      voteRepository.getVoteCats(
        sortBy = sortBy,
        limit = limit,
      )
  }
