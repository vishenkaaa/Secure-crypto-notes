package com.example.domain.repository

import com.example.domain.model.coin.Coin
import com.example.domain.model.coin.CoinDetails
import com.example.domain.model.coin.MarketChart

interface CoinRepository {
    suspend fun getTopCoins(limit: Int = 10): List<Coin>
    suspend fun getCoinDetails(id: String): CoinDetails
    suspend fun getMarketChart(id: String, days: Int = 7): MarketChart
}
