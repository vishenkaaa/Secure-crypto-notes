package com.example.data.repositories

import com.example.data.local.db.dao.CoinDao
import com.example.data.local.db.entities.toDomain
import com.example.data.local.db.entities.toEntity
import com.example.data.remote.coingecko.api.CoinGeckoApi
import com.example.data.remote.coingecko.mapper.toDomain
import com.example.domain.model.coin.Coin
import com.example.domain.model.coin.CoinDetails
import com.example.domain.model.coin.MarketChart
import com.example.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi,
    private val coinDao: CoinDao
) : CoinRepository {
    override suspend fun getTopCoins(limit: Int): List<Coin> =
        api.getTopCoins(perPage = limit).map { it.toDomain() }

    override suspend fun getCoinDetails(id: String): CoinDetails =
        api.getCoinDetails(id).toDomain()

    override suspend fun getMarketChart(id: String, days: Int): MarketChart =
        api.getMarketChart(id, days = days).toDomain()

    override fun getFavoriteCoins(): Flow<List<Coin>> =
        coinDao.getFavoriteCoins().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun addFavoriteCoin(coin: Coin) {
        coinDao.insertFavoriteCoin(coin.toEntity())
    }

    override suspend fun removeFavoriteCoin(coin: Coin) {
        coinDao.deleteFavoriteCoin(coin.toEntity())
    }
}
