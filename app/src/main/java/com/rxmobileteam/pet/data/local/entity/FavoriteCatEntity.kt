package com.rxmobileteam.pet.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rxmobileteam.pet.domain.model.Cat
import java.time.Instant

@Entity(tableName = "favorite_cats")
data class FavoriteCatEntity(
  @PrimaryKey
  @ColumnInfo(name = "id")
  val id: String,
  @ColumnInfo(name = "url")
  val url: String,
  @ColumnInfo(name = "width")
  val width: Int,
  @ColumnInfo(name = "height")
  val height: Int,
  @ColumnInfo(name = "created_at")
  val createdAt: Instant,
)

fun FavoriteCatEntity.toCatDomain(): Cat = Cat(
  id = id,
  url = url,
  width = width,
  height = height,
)
