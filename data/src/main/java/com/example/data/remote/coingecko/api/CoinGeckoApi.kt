package com.example.data.remote.coingecko.api

import com.example.data.remote.coingecko.dto.CoinDetailsDto
import com.example.data.remote.coingecko.dto.CoinDto
import com.example.data.remote.coingecko.dto.MarketChartDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("coins/markets")
    suspend fun getTopCoins(
        @Query("vs_currency") vs: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): List<CoinDto>

    @GET("coins/{id}")
    suspend fun getCoinDetails(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false
    ): CoinDetailsDto

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") vs: String = "usd",
        @Query("days") days: Int = 7
    ): MarketChartDto
}