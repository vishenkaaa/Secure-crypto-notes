package com.example.presentation.feature.home.crypto.coinDetails

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.model.coin.CoinDetails
import com.example.domain.model.coin.MarketChart
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.common.ui.modifier.softShadow
import com.example.presentation.common.ui.values.Green
import com.example.presentation.common.ui.values.Red
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoinDetailsRoute(
    viewModel: CoinDetailsVM = hiltViewModel(),
    coinId: String,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadCoinDetails(coinId)
    }

    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    CoinDetailsScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        clearErrors = viewModel::clearErrors,
        retryLastAction = viewModel::retryLastAction,
        hasRetryAction = viewModel.hasRetryAction(),
        onBack = onBack
    )
}

@Composable
fun CoinDetailsScreen(
    uiState: CoinDetailsUiState,
    baseUiState: BaseUiState,
    clearErrors: () -> Unit,
    retryLastAction: () -> Unit,
    hasRetryAction: Boolean = false,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    Box {
        Scaffold(
            topBar = {
                CenterAlignedHeader(uiState.coinDetails.name){onBack()}
            }
        ) { padding ->
            if(!baseUiState.isLoading && baseUiState.error==null)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CoinLogo(
                                    imageUrl = uiState.coinDetails.image,
                                    coinName = uiState.coinDetails.name
                                )

                                uiState.coinDetails.marketCapRank?.let { rank ->
                                    RankBadge(rank = rank)
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                PriceCard(coinDetails = uiState.coinDetails)

                                uiState.coinDetails.homepage?.takeIf { it.isNotBlank() }
                                    ?.let { homepage ->
                                        WebsiteCard(
                                            homepage = homepage,
                                            onWebsiteClick = { url ->
                                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                                context.startActivity(intent)
                                            }
                                        )
                                    }
                            }
                        }
                    }

                    uiState.marketChart?.let { marketChart ->
                        item {
                            PriceChartCard(
                                marketChart = marketChart,
                            )
                        }
                    }

                    uiState.coinDetails.description.takeIf { it.isNotBlank() }?.let { description ->
                        item {
                            DescriptionCard(
                                coinName = uiState.coinDetails.name,
                                description = description
                            )
                        }
                    }

                    item {
                        Spacer(Modifier.height(20.dp))
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
}

@SuppressLint("DefaultLocale")
@Composable
private fun PriceChartCard(
    marketChart: MarketChart,
) {
    val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 8.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.price_chart_7_days),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AndroidView(
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        setTouchEnabled(true)
                        isDragEnabled = true
                        setScaleEnabled(true)
                        setPinchZoom(true)

                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            textColor = onPrimaryContainerColor.toArgb()
                            xAxis.granularity = 24 * 60 * 60 * 1000f
                            xAxis.isGranularityEnabled = true
                            xAxis.setLabelCount(7, true)
                            valueFormatter = object : ValueFormatter() {
                                private val sdf = SimpleDateFormat("dd/MM", Locale.US)
                                override fun getFormattedValue(value: Float): String {
                                    return sdf.format(Date(value.toLong()))
                                }
                            }
                        }

                        axisLeft.apply {
                            setDrawGridLines(true)
                            textColor = onPrimaryContainerColor.toArgb()
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return "$${String.format("%.2f", value)}"
                                }
                            }
                        }

                        axisRight.isEnabled = false
                        legend.isEnabled = false

                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                update = { chart ->
                    val entries = marketChart.prices.mapIndexed { _, (timestamp, price) ->
                        Entry(timestamp.toFloat(), price.toFloat())
                    }

                    val dataSet = LineDataSet(entries, "Price").apply {
                        color = primaryColor.toArgb()
                        lineWidth = 2f
                        setDrawCircles(false)
                        setDrawValues(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        setDrawFilled(true)
                        fillColor = primaryColor.copy(alpha = 0.3f).toArgb()
                    }

                    chart.data = LineData(dataSet)
                    chart.animateX(1000)
                    chart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun CoinLogo(
    imageUrl: String?,
    coinName: String
) {
    Card(
        shape = CircleShape,
        modifier = Modifier
            .size(110.dp)
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 8.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 100.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = coinName,
                modifier = Modifier.size(72.dp)
            )
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 8.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = stringResource(R.string.rank, rank),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
        )
    }
}

@Composable
private fun PriceCard(coinDetails: CoinDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 8.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.current_price),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = stringResource(R.string.usd, "%,.2f".format(coinDetails.currentPrice)),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            PriceChangeIndicator(priceChange = coinDetails.priceChange24h)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun PriceChangeIndicator(priceChange: Double) {
    val priceChangeColor = if (priceChange >= 0) Green else Red
    val priceChangePrefix = if (priceChange >= 0) "+" else ""

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(
                if (priceChange >= 0) R.drawable.ic_trending_up
                else R.drawable.ic_trending_down
            ),
            contentDescription = null,
            tint = priceChangeColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$priceChangePrefix${String.format("%.2f", priceChange)}%",
            color = priceChangeColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = stringResource(R.string._24h),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun WebsiteCard(
    homepage: String,
    onWebsiteClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 8.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onWebsiteClick(homepage) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_link),
                contentDescription = "Website",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.visit_website),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun DescriptionCard(
    coinName: String,
    description: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 8.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                expanded = !expanded
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.about, coinName),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                lineHeight = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (expanded) Int.MAX_VALUE else 5,
            )
            if (!expanded)
                Text(
                    text = stringResource(R.string.show_more),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            else
                Text(
                    text = stringResource(R.string.show_less),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoinDetailsScreenPreview() {
    val sampleCoinDetails = CoinDetails(
        id = "bitcoin",
        name = "Bitcoin",
        symbol = "BTC",
        description = "Bitcoin is a decentralized cryptocurrency originally described in a 2008 whitepaper by a person, or group of people, using the alias Satoshi Nakamoto. It was launched soon after, in January 2009. Bitcoin is a peer-to-peer online currency, meaning that all transactions happen directly between equal, independent network participants, without the need for any intermediary to permit or facilitate them.",
        homepage = "https://bitcoin.org",
        image = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
        marketCapRank = 1,
        currentPrice = 45230.50,
        priceChange24h = 2.45
    )

    MaterialTheme {
        CoinDetailsScreen(
            uiState = CoinDetailsUiState(coinDetails = sampleCoinDetails),
            baseUiState = BaseUiState(),
            clearErrors = {},
            retryLastAction = {},
            onBack = {}
        )
    }
}