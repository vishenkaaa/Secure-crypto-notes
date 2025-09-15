package com.example.data.remote.coingecko.mapper

import com.example.data.remote.coingecko.dto.CoinDto
import com.example.domain.model.coin.Coin

fun CoinDto.toDomain() = Coin(
    id = id,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = currentPrice,
)
