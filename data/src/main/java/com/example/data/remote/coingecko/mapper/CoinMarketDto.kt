package com.example.data.remote.coingecko.mapper

import com.example.data.remote.coingecko.dto.CoinMarketDto
import com.example.domain.model.coin.Coin

fun CoinMarketDto.toDomain() = Coin(
    id = id,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = currentPrice,
)
