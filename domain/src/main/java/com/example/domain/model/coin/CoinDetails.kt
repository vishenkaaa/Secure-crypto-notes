package com.example.domain.model.coin

data class CoinDetails(
    val id: String,
    val name: String,
    val symbol: String,
    val description: String,
    val homepage: String?,
    val image: String?,
    val marketCapRank: Int?,
    val currentPrice: Double,
    val priceChange24h: Double
)