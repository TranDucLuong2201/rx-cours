package com.rxmobileteam.pet.data.repository

import com.rxmobileteam.pet.data.mapper.toCatDomain
import com.rxmobileteam.pet.data.remote.PetService
import com.rxmobileteam.pet.domain.repository.VoteCatRepository
import com.rxmobileteam.pet.utils.AppDispatcher
import com.rxmobileteam.pet.utils.DispatcherType
import com.rxmobileteam.pet.utils.runSuspendCatching
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class VoteCatRepositoryImpl
  @Inject
  constructor(
    private val petService: PetService,
    @AppDispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
  ) : VoteCatRepository {
    override suspend fun getVoteCats(
      sortBy: String,
      limit: Int,
    ) = runSuspendCatching(ioDispatcher) {
      petService
        .getVoteList(
          order = sortBy,
          limit = limit,
        )
        .map { it.toCatDomain() }
    }
  }
