package com.example.domain.usecase.coin

import com.example.domain.model.coin.Coin
import com.example.domain.repository.CoinRepository
import javax.inject.Inject

class GetTopCoinsUseCase @Inject constructor (
    private val repository: CoinRepository
) {
    suspend operator fun invoke(limit: Int = 10): List<Coin> =
        repository.getTopCoins(limit)
}