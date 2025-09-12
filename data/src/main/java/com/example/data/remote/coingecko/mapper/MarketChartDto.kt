package com.example.data.remote.coingecko.mapper

import com.example.data.remote.coingecko.dto.MarketChartDto
import com.example.domain.model.coin.MarketChart

fun MarketChartDto.toDomain() = MarketChart(
    prices = prices.map { it[0].toLong() to it[1] }
)