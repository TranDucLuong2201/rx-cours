package com.rxmobileteam.pet.data.di

import com.rxmobileteam.pet.data.repository.FavoriteCatRepositoryImpl
import com.rxmobileteam.pet.data.repository.VoteCatRepositoryImpl
import com.rxmobileteam.pet.data.utils.ConnectivityManagerNetworkMonitor
import com.rxmobileteam.pet.data.utils.NetworkMonitor
import com.rxmobileteam.pet.domain.repository.FavoriteCatRepository
import com.rxmobileteam.pet.domain.repository.VoteCatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
  @Binds
  fun bindVoteCatRepository(repository: VoteCatRepositoryImpl): VoteCatRepository

  @Binds
  fun favoriteCatRepositoryImpl(repository: FavoriteCatRepositoryImpl): FavoriteCatRepository

  @Binds
  @Singleton
  fun bindNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor
}
