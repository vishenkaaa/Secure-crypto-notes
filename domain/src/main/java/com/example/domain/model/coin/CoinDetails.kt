package com.example.domain.model.coin

data class CoinDetails(
    val id: String = "",
    val name: String = "",
    val symbol: String = "",
    val description: String = "",
    val homepage: String? = null,
    val image: String? = null,
    val marketCapRank: Int? = null,
    val currentPrice: Double = 0.0,
    val priceChange24h: Double = 0.0
)