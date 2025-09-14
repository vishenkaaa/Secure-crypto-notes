package com.example.presentation.feature.home.crypto

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.model.coin.Coin
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.common.ui.modifier.softShadow
import com.example.presentation.common.ui.values.Orange
import com.example.presentation.common.ui.values.SecureCryptoNotesTheme
import com.example.presentation.feature.home.crypto.model.CryptoUiState

@SuppressLint("FrequentlyChangingValue")
@Composable
fun CryptoRoute(
    viewModel: CryptoVM = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.scrollState.firstVisibleItemIndex, uiState.scrollState.firstVisibleItemScrollOffset) {
        viewModel.updateScrollState(uiState.scrollState)
    }

    CryptoScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        toggleFavorite = viewModel::toggleFavorite,
        clearErrors = viewModel::clearErrors,
        retryLastAction = viewModel::retryLastAction,
        hasRetryAction = viewModel.hasRetryAction()
    )
}

@SuppressLint("FrequentlyChangingValue")
@Composable
fun CryptoScreen(
    uiState: CryptoUiState,
    baseUiState: BaseUiState,
    toggleFavorite: (Coin) -> Unit = {},
    clearErrors: () -> Unit,
    retryLastAction: () -> Unit,
    hasRetryAction: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = { CenterAlignedHeader(stringResource(R.string.app_name)) },
        ) { padding ->
            if (uiState.coins.isNotEmpty())
                LazyColumn(
                    state = uiState.scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.coins) { coin ->
                        CoinItem(
                            coin = coin,
                            onFavoriteClick = toggleFavorite
                        )
                    }
                    item { Spacer(Modifier.height(60.dp)) }
                }
            else
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.empty),
                        contentDescription = "Empty",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
        }
    }

    HandleError(
        baseUiState = baseUiState,
        onErrorConsume = clearErrors,
        onRetry = if (hasRetryAction) retryLastAction else null
    )

    LoadingBackground(baseUiState.isLoading)
}

@Composable
fun CoinItem(
    coin: Coin,
    onClick: (Coin) -> Unit = {},
    onFavoriteClick: (Coin) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 12.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(coin) },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.image,
                contentDescription = "Coin img",
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = coin.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = coin.symbol,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { onFavoriteClick(coin) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = if (coin.isFavorite) painterResource(R.drawable.ic_star_filled)
                            else painterResource(R.drawable.ic_star),
                            contentDescription = "favorite",
                            tint = Orange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.usd, coin.currentPrice),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CryptoScreenPreview() {
    val sampleCoins = listOf(
        Coin(
            id = "bitcoin",
            symbol = "BTC",
            name = "Bitcoin",
            image = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
            currentPrice = 45230.50,
            isFavorite = true
        ),
        Coin(
            id = "ethereum",
            symbol = "ETH",
            name = "Ethereum",
            image = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
            currentPrice = 2845.75,
            isFavorite = false
        ),
    )

    val uiState = CryptoUiState(coins = sampleCoins)
    val baseUiState = BaseUiState()

    SecureCryptoNotesTheme {
        CryptoScreen(
            uiState = uiState,
            baseUiState = baseUiState,
            clearErrors = {},
            retryLastAction = {},
            hasRetryAction = false
        )
    }
}