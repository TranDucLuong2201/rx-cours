package com.rxmobileteam.pet.domain.repository

import com.rxmobileteam.pet.domain.model.Cat
import kotlinx.coroutines.flow.Flow

interface FavoriteCatRepository {
  suspend fun voteDown(cat: Cat): Result<Unit>
  suspend fun voteUp(cat: Cat): Result<Unit>
  fun observeFavoriteCats(): Flow<Result<List<Cat>>>
}
