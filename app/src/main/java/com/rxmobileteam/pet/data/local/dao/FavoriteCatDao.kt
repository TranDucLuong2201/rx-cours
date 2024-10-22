package com.rxmobileteam.pet.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rxmobileteam.pet.data.local.entity.FavoriteCatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCatDao {
  @Query("SELECT * FROM favorite_cats ORDER BY created_at DESC")
  fun observeAll(): Flow<List<FavoriteCatEntity>>

  @Query("SELECT * FROM favorite_cats WHERE id = :id")
  fun observeById(id: String): Flow<FavoriteCatEntity?>

  @Insert
  suspend fun insertMany(cats: List<FavoriteCatEntity>)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(cat: FavoriteCatEntity): Long

  @Update
  suspend fun update(cat: FavoriteCatEntity)

  @Delete
  suspend fun delete(cat: FavoriteCatEntity)
}
