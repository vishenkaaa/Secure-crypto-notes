package com.example.data.remote.coingecko.mapper

import com.example.data.remote.coingecko.dto.CoinDetailsDto
import com.example.domain.model.coin.CoinDetails

fun CoinDetailsDto.toDomain() = CoinDetails(
    id = id,
    symbol = symbol,
    name = name,
    description = description["en"] ?: "",
    homepage = links.homepage.firstOrNull().orEmpty(),
    marketCapRank = marketCapRank,
    currentPrice = marketData.currentPrice["usd"] ?: 0.0,
    image = image.large,
    priceChange24h = marketData.priceChangePercentage24h
)
