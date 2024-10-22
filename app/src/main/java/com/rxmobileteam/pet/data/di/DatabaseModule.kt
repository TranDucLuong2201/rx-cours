package com.rxmobileteam.pet.data.di

import android.content.Context
import com.rxmobileteam.pet.data.local.PetsAppDatabase
import com.rxmobileteam.pet.data.local.dao.FavoriteCatDao
import com.rxmobileteam.pet.utils.AppDispatcher
import com.rxmobileteam.pet.utils.DispatcherType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {
  companion object {
    @Provides
    @Singleton
    fun providePetsAppDatabase(
      @ApplicationContext appContext: Context,
      @AppDispatcher(DispatcherType.IO) ioDispatcher: CoroutineDispatcher,
    ): PetsAppDatabase =
      PetsAppDatabase.getInstance(
        context = appContext,
        queryExecutor = ioDispatcher.asExecutor(),
      )

    @Provides
    fun provideFavoriteCatDao(petsAppDatabase: PetsAppDatabase): FavoriteCatDao = petsAppDatabase.favoriteCatDao()
  }
}
