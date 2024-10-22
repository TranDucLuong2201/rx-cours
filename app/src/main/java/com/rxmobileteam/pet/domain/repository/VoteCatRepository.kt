package com.rxmobileteam.pet.domain.repository

import com.rxmobileteam.pet.domain.model.Cat

interface VoteCatRepository {
  suspend fun getVoteCats(
    sortBy: String,
    limit: Int,
  ): Result<List<Cat>>
}
