package com.rxmobileteam.pet.data.repository

import com.hoc081098.flowext.FlowExtPreview
import com.hoc081098.flowext.mapToResult
import com.rxmobileteam.pet.data.local.dao.FavoriteCatDao
import com.rxmobileteam.pet.data.local.entity.FavoriteCatEntity
import com.rxmobileteam.pet.data.local.entity.toCatDomain
import com.rxmobileteam.pet.domain.model.Cat
import com.rxmobileteam.pet.domain.repository.FavoriteCatRepository
import com.rxmobileteam.pet.utils.AppDispatcher
import com.rxmobileteam.pet.utils.DispatcherType
import com.rxmobileteam.pet.utils.runSuspendCatching
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class FavoriteCatRepositoryImpl
  @Inject
  constructor(
    private val favoriteCatDao: FavoriteCatDao,
    @AppDispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher,
  ) : FavoriteCatRepository {
    override suspend fun voteDown(cat: Cat) =
      runSuspendCatching(ioDispatcher) { favoriteCatDao.delete(cat.toFavoriteCatEntity()) }

    override suspend fun voteUp(cat: Cat) =
      runSuspendCatching(ioDispatcher) {
        favoriteCatDao.insert(cat.toFavoriteCatEntity())
        Unit
      }

    @OptIn(FlowExtPreview::class)
    override fun observeFavoriteCats() =
      favoriteCatDao
        .observeAll()
        .map { it.map(FavoriteCatEntity::toCatDomain) }
        .flowOn(ioDispatcher)
        .mapToResult()
  }

internal fun Cat.toFavoriteCatEntity(): FavoriteCatEntity =
  FavoriteCatEntity(
    id = id,
    url = url,
    width = width,
    height = height,
    createdAt = Instant.now(),
  )
