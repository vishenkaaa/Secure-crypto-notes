package com.example.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.coin.Coin

@Entity(tableName = "favorite_coins")
data class CoinEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double
)

fun CoinEntity.toDomain() = Coin(id, symbol, name, image, currentPrice, true)
fun Coin.toEntity() = CoinEntity(id, symbol, name, image, currentPrice)