package com.example.presentation.feature.auth

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.AuthRepository
import com.example.domain.usecase.auth.HasPinUseCase
import com.example.domain.usecase.auth.SavePinUseCase
import com.example.domain.usecase.auth.VerifyPinUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.common.utils.AuthState
import com.example.presentation.common.utils.BiometricHelper
import com.example.presentation.feature.auth.model.AuthScreenUIState
import com.example.presentation.feature.auth.model.AuthStep
import com.example.presentation.feature.auth.model.BiometricState
import com.example.presentation.common.utils.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val biometricHelper: BiometricHelper,
    private val hasPinUseCase: HasPinUseCase,
    private val savePinUseCase: SavePinUseCase,
    private val verifyPinUseCase: VerifyPinUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        AuthScreenUIState(canUseBiometric = biometricHelper.canUseBiometric())
    )
    val uiState = _uiState.asStateFlow()

    private val _biometricState = MutableStateFlow<BiometricState>(BiometricState.Idle)
    val biometricState = _biometricState.asStateFlow()

    init {
        initializeAuthFlow()
    }

    private fun initializeAuthFlow() {
        viewModelScope.launch {
            try {
                handleLoading(true)
                val hasPin = hasPinUseCase()
                val canUseBiometric = biometricHelper.canUseBiometric()

                if (!hasPin) {
                    _uiState.update {
                        it.copy(
                            step = AuthStep.ENTER_PIN,
                            isCreatingPin = true,
                            canUseBiometric = canUseBiometric,
                            showBiometric = false
                        )
                    }
                } else {
                    if (canUseBiometric) {
                        _uiState.update {
                            it.copy(
                                step = AuthStep.BIOMETRIC,
                                isCreatingPin = false,
                                canUseBiometric = true,
                                showBiometric = true
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                step = AuthStep.ENTER_PIN,
                                isCreatingPin = false,
                                canUseBiometric = false,
                                showBiometric = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                handleError(e) { initializeAuthFlow() }
            }
            finally {
                handleLoading(false)
            }
        }
    }

    fun switchToPinMode() {
        _uiState.update {
            it.copy(
                step = AuthStep.ENTER_PIN,
                showBiometric = false,
                currentPin = "",
                confirmPin = ""
            )
        }
        _biometricState.value = BiometricState.Idle
    }

    fun switchToBiometricMode() {
        if (_uiState.value.canUseBiometric && !_uiState.value.isCreatingPin) {
            _uiState.update {
                it.copy(
                    step = AuthStep.BIOMETRIC,
                    showBiometric = true,
                    currentPin = "",
                    confirmPin = ""
                )
            }
        }
    }

    fun authenticateWithBiometric(activity: FragmentActivity) {
        if (!_uiState.value.canUseBiometric || _uiState.value.isCreatingPin) return

        viewModelScope.launch {
            try {
                _biometricState.value = BiometricState.Authenticating
                clearErrors()

                val biometricPrompt = biometricHelper.createPrompt(activity, createBiometricCallback())
                val promptInfo = biometricHelper.createPromptInfo(
                    title = context.getString(R.string.biometric_prompt_title),
                    subtitle = context.getString(R.string.biometric_prompt_subtitle),
                    negativeButton = context.getString(R.string.biometric_prompt_use_pin)
                )

                biometricPrompt.authenticate(promptInfo)

            } catch (e: Exception) {
                _biometricState.value = BiometricState.Error(
                    context.getString(R.string.error_biometric_init)
                )
                handleError(e) { authenticateWithBiometric(activity) }
            }
        }
    }

    private fun createBiometricCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        switchToPinMode()
                    }
                    BiometricPrompt.ERROR_USER_CANCELED -> {
                        _biometricState.value = BiometricState.UserCanceled
                        switchToPinMode()
                    }
                    else -> {
                        _biometricState.value = BiometricState.Error(
                            context.getString(
                                R.string.error_biometric_auth,
                                errString
                            ))
                        switchToPinMode()
                    }
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                _biometricState.value = BiometricState.Success
                viewModelScope.launch {
                    authStateManager.setAuthState(AuthState.Authenticated)
                }
            }

            override fun onAuthenticationFailed() {
                _biometricState.value = BiometricState.Failed
            }
        }
    }

    fun addDigit(digit: String) {
        val current = _uiState.value
        if (baseUiState.value.isLoading) return

        when (current.step) {
            AuthStep.ENTER_PIN -> {
                if (current.currentPin.length < 4) {
                    val newPin = current.currentPin + digit
                    _uiState.update { it.copy(currentPin = newPin) }
                    clearErrors()

                    if (newPin.length == 4) {
                        viewModelScope.launch {
                            if (!current.isCreatingPin) handleLoading(true)
                            delay(150)
                            if (current.isCreatingPin) {
                                _uiState.update { it.copy(step = AuthStep.CONFIRM_PIN) }
                            } else {
                                verifyPin(newPin)
                            }
                        }
                    }
                }
            }
            AuthStep.CONFIRM_PIN -> {
                if (current.confirmPin.length < 4) {
                    val newConfirmPin = current.confirmPin + digit
                    _uiState.update { it.copy(confirmPin = newConfirmPin) }
                    clearErrors()

                    if (newConfirmPin.length == 4) {
                        viewModelScope.launch {
                            delay(150)
                            if (newConfirmPin == current.currentPin) {
                                createPin(current.currentPin)
                            } else {
                                resetAuthState()
                                handleError(Exception(context.getString(R.string.error_pin_mismatch)))
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }

    fun removeDigit() {
        val current = _uiState.value
        if (baseUiState.value.isLoading) return

        when (current.step) {
            AuthStep.ENTER_PIN -> {
                if (current.currentPin.isNotEmpty()) {
                    _uiState.update { it.copy(currentPin = current.currentPin.dropLast(1)) }
                    clearErrors()
                }
            }
            AuthStep.CONFIRM_PIN -> {
                if (current.confirmPin.isNotEmpty()) {
                    _uiState.update { it.copy(confirmPin = current.confirmPin.dropLast(1)) }
                    clearErrors()
                } else {
                    _uiState.update { it.copy(step = AuthStep.ENTER_PIN) }
                }
            }
            else -> {}
        }
    }

    private fun createPin(pin: String) {
        viewModelScope.launch {
            try {
                handleLoading(true)
                savePinUseCase(pin)
                authStateManager.setAuthState(AuthState.Authenticated)
                _uiState.update { it.copy(isComplete = true) }
            } catch (e: Exception) {
                resetAuthState()
                handleError(e) { createPin(pin) }
            }
            finally {
                handleLoading(false)
            }
        }
    }

    private fun verifyPin(pin: String) {
        handleLoading(true)
        viewModelScope.launch {
            try {
                val isValid = verifyPinUseCase(pin)

                if (isValid) {
                    authStateManager.setAuthState(AuthState.Authenticated)
                    _uiState.update { it.copy(isComplete = true) }
                } else {
                    handleError(Exception(context.getString(R.string.error_incorrect_pin)))
                    _uiState.update { it.copy(currentPin = "") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(currentPin = "") }
                handleError(e) { verifyPin(pin) }
            } finally {
                handleLoading(false)
            }
        }
    }

    private fun resetAuthState() {
        _uiState.update {
            it.copy(
                currentPin = "",
                confirmPin = "",
                step = if (it.isCreatingPin) AuthStep.ENTER_PIN else AuthStep.ENTER_PIN
            )
        }
    }
}