package com.example.data.remote.coingecko.dto

import com.google.gson.annotations.SerializedName

data class CoinDetailsDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: Map<String, String>,

    @SerializedName("links")
    val links: LinksDto,

    @SerializedName("market_cap_rank")
    val marketCapRank: Int,

    @SerializedName("market_data")
    val marketData: MarketDataDto,

    @SerializedName("image")
    val image: ImageDto
) {
    data class LinksDto(
        @SerializedName("homepage")
        val homepage: List<String>
    )

    data class MarketDataDto(
        @SerializedName("current_price")
        val currentPrice: Map<String, Double>,

        @SerializedName("price_change_percentage_24h")
        val priceChangePercentage24h: Double
    )

    data class ImageDto(
        @SerializedName("large")
        val large: String
    )
}