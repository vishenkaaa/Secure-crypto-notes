package com.example.domain.usecase.coin

import com.example.domain.model.coin.CoinDetails
import com.example.domain.repository.CoinRepository
import javax.inject.Inject


class GetCoinDetailsUseCase @Inject constructor (
    private val repository: CoinRepository
) {
    suspend operator fun invoke(id: String): CoinDetails =
        repository.getCoinDetails(id)
}