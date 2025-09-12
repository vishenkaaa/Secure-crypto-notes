package com.example.presentation.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.common.ui.values.SecureCryptoNotesTheme
import com.example.presentation.feature.auth.model.AuthScreenUIState
import com.example.presentation.feature.auth.model.AuthStep
import com.example.presentation.feature.auth.model.BiometricState
import kotlinx.coroutines.delay

@Composable
fun AuthRoute(
    viewModel: AuthVM = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()
    val biometricState by viewModel.biometricState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.step, uiState.showBiometric) {
        if (uiState.step == AuthStep.BIOMETRIC && uiState.showBiometric) {
            viewModel.authenticateWithBiometric(activity)
        }
    }

    LaunchedEffect(biometricState) {
        if (biometricState is BiometricState.Success) {
            delay(500)
        }
    }

    AuthScreen(
        uiState, baseUiState,
        addDigit = viewModel::addDigit,
        removeDigit = viewModel::removeDigit,
        onBiometricClick = viewModel::switchToBiometricMode,
        clearErrors = viewModel::clearErrors,
        retryLastAction = viewModel::retryLastAction,
        hasRetryAction = viewModel.hasRetryAction()
    )
}

@Composable
fun AuthScreen(
    uiState: AuthScreenUIState,
    baseUIState: BaseUiState,
    addDigit: (String) -> Unit,
    removeDigit: () -> Unit,
    onBiometricClick: () -> Unit,
    clearErrors: () -> Unit,
    retryLastAction: () -> Unit,
    hasRetryAction: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            topBar = {
                if (uiState.isCreatingPin) {
                    CenterAlignedHeader(
                        title = when (uiState.step) {
                            AuthStep.ENTER_PIN -> stringResource(R.string.create_pin)
                            AuthStep.CONFIRM_PIN -> stringResource(R.string.confirm_pin)
                            else -> ""
                        }
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                PinContent(
                    uiState = uiState,
                    onDigitClick = addDigit,
                    onDeleteClick = removeDigit,
                    onBiometricClick = onBiometricClick,
                    isEnabled = !baseUIState.isLoading
                )
            }
        }

        HandleError(
            baseUiState = baseUIState,
            onErrorConsume = clearErrors,
            onRetry = if (hasRetryAction) retryLastAction else null
        )

        LoadingBackground(baseUIState.isLoading)
    }
}

@Composable
private fun PinContent(
    uiState: AuthScreenUIState,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: () -> Unit,
    isEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = getAuthHeaderText(uiState),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 28.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        PinIndicators(
            currentPin = getCurrentDisplayPin(uiState),
            modifier = Modifier.padding(bottom = 60.dp)
        )

        PinKeypad(
            onDigitClick = onDigitClick,
            onDeleteClick = onDeleteClick,
            onBiometricClick = onBiometricClick,
            showBiometric = uiState.canUseBiometric && !uiState.isCreatingPin,
            isEnabled = isEnabled
        )
    }
}

@Composable
private fun PinIndicators(
    currentPin: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        repeat(4) { index ->
            val isFilled = index < currentPin.length
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = if (isFilled) Color.White else Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (isFilled) Color.White else Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun PinKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: () -> Unit,
    showBiometric: Boolean,
    isEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        for (row in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                for (col in 0..2) {
                    val number = row * 3 + col + 1
                    KeypadButton(
                        text = number.toString(),
                        onClick = { onDigitClick(number.toString()) },
                        enabled = isEnabled
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            if (showBiometric) {
                IconButton(
                    onClick = onBiometricClick,
                    enabled = isEnabled,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (isEnabled) 0.1f else 0.05f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fingerprint),
                        contentDescription = stringResource(R.string.use_biometric),
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (isEnabled) 1f else 0.5f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(72.dp))
            }

            KeypadButton(
                text = "0",
                onClick = { onDigitClick("0") },
                enabled = isEnabled
            )

            IconButton(
                onClick = onDeleteClick,
                enabled = isEnabled,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (isEnabled) 0.1f else 0.05f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_backspace),
                    contentDescription = "Delete",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (isEnabled) 1f else 0.5f)
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = if (enabled) 0.2f else 0.1f))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                ),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (enabled) 1f else 0.5f)
        )
    }
}

@Composable
private fun getAuthHeaderText(uiState: AuthScreenUIState): String {
    return when {
        uiState.isCreatingPin && uiState.step == AuthStep.ENTER_PIN -> stringResource(R.string.create_4_digit_pin)
        uiState.isCreatingPin && uiState.step == AuthStep.CONFIRM_PIN -> stringResource(R.string.confirm_pin_code)
        else -> stringResource(R.string.enter_pin_code)
    }
}

private fun getCurrentDisplayPin(uiState: AuthScreenUIState): String {
    return when (uiState.step) {
        AuthStep.ENTER_PIN -> uiState.currentPin
        AuthStep.CONFIRM_PIN -> uiState.confirmPin
        else -> ""
    }
}

@Preview
@Composable
fun AuthScreenPreview(){
    SecureCryptoNotesTheme {
        AuthScreen(AuthScreenUIState(), BaseUiState(), {}, {}, {}, {}, {})
    }
}