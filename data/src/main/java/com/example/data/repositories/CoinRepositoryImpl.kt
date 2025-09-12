package com.example.data.repositories

import com.example.data.remote.coingecko.api.CoinGeckoApi
import com.example.data.remote.coingecko.mapper.toDomain
import com.example.domain.model.coin.Coin
import com.example.domain.model.coin.CoinDetails
import com.example.domain.model.coin.MarketChart
import com.example.domain.repository.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi
) : CoinRepository {
    override suspend fun getTopCoins(limit: Int): List<Coin> =
        api.getTopCoins(perPage = limit).map { it.toDomain() }

    override suspend fun getCoinDetails(id: String): CoinDetails =
        api.getCoinDetails(id).toDomain()

    override suspend fun getMarketChart(id: String, days: Int): MarketChart =
        api.getMarketChart(id, days = days).toDomain()
}
