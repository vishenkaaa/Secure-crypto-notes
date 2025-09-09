package com.example.presentation.navigation

enum class TopLevelDestinations(
    val route: Home,
    val contentDescription: String
) {
    NOTES(
        Home.Notes,
        "Notes"
    ),
    CRYPTO(
        Home.Crypto,
        "Crypto"
    ),
}