package com.example.domain.usecase.coin

import com.example.domain.model.coin.Coin
import com.example.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteCoinsUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<List<Coin>> {
        return repository.getFavoriteCoins()
    }
}