package com.example.data.remote.coingecko.dto

import com.google.gson.annotations.SerializedName

data class CoinMarketDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("current_price")
    val currentPrice: Double,
)
