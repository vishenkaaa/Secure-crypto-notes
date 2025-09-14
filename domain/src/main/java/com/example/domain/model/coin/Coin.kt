    package com.example.domain.model.coin

    data class Coin(
        val id: String,
        val symbol: String,
        val name: String,
        val image: String,
        val currentPrice: Double,
        val isFavorite: Boolean = false
    )