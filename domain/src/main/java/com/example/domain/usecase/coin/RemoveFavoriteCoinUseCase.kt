package com.example.domain.usecase.coin

import com.example.domain.model.coin.Coin
import com.example.domain.repository.CoinRepository
import javax.inject.Inject

class RemoveFavoriteCoinUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(coin: Coin): Result<Unit> =
        try {
            repository.removeFavoriteCoin(coin)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}