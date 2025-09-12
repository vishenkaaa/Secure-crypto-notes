package com.example.domain.model.coin

data class MarketChart(
    val prices: List<Pair<Long, Double>>
)