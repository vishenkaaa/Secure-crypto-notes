package com.example.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Graphs {
    @Serializable
    data object IdleScreen : Graphs()

    @Serializable
    data object Login : Graphs()

    @Serializable
    data object Home : Graphs()
}

@Serializable
sealed class Login {

    @Serializable
    data object Biometric : Login()

    @Serializable
    data object Pin : Login()
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