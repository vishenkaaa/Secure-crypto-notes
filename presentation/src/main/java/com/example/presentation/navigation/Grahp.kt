package com.example.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Graphs {
    @Serializable
    data object Splash : Graphs()

    @Serializable
    data object Auth : Graphs()

    @Serializable
    data object Home : Graphs()
}

@Serializable
sealed class Home {

    @Serializable
    data object Notes : Home()

    @Serializable
    data object Crypto : Home()

    @Serializable
    data object CryptoDetails : Home()
}