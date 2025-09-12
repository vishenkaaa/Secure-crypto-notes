package com.example.domain.usecase.coin

import com.example.domain.model.coin.MarketChart
import com.example.domain.repository.CoinRepository
import javax.inject.Inject

class GetMarketChartUseCase @Inject constructor (
    private val repository: CoinRepository
) {
    suspend operator fun invoke(id: String, days: Int = 7): MarketChart =
        repository.getMarketChart(id, days)
}