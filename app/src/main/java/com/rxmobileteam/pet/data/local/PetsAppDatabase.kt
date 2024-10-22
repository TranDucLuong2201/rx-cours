package com.rxmobileteam.pet.data.local

import android.content.Context
import androidx.annotation.AnyThread
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.rxmobileteam.pet.data.local.dao.FavoriteCatDao
import com.rxmobileteam.pet.data.local.entity.FavoriteCatEntity
import java.time.Instant
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Database(
  entities = [
    FavoriteCatEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
@TypeConverters(
  InstantTypeConverter::class,
)
abstract class PetsAppDatabase : RoomDatabase() {
  abstract fun favoriteCatDao(): FavoriteCatDao

  companion object {
    private const val DB_NAME = "pets_app.db"

    // Double-checked locking singleton pattern
    @Volatile
    private var INSTANCE: PetsAppDatabase? = null

    @AnyThread
    @JvmStatic
    fun getInstance(
      context: Context,
      queryExecutor: Executor,
    ): PetsAppDatabase =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          PetsAppDatabase::class.java,
          DB_NAME,
        )
          // Use a separate thread for Room transactions to avoid deadlocks. This means that tests that run Room
          // transactions can't use testCoroutines.scope.runBlockingTest, and have to simply use runBlocking instead.
          .setTransactionExecutor(Executors.newSingleThreadExecutor())
          // Run queries on background I/O thread.
          .setQueryExecutor(queryExecutor)
          .build()
          .also { INSTANCE = it }
      }
  }
}

@Suppress("unused")
class InstantTypeConverter {
  @TypeConverter
  fun fromInstant(instant: Instant?): Long? = instant?.toEpochMilli()

  @TypeConverter
  fun toInstant(millis: Long?): Instant? = millis?.let { Instant.ofEpochMilli(it) }
}
