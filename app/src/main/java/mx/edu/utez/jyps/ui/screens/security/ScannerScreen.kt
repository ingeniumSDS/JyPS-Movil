package mx.edu.utez.jyps.ui.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.jyps.BuildConfig
import mx.edu.utez.jyps.ui.components.common.AppToast
import mx.edu.utez.jyps.ui.components.dialogs.ScanResultDialog
import mx.edu.utez.jyps.ui.components.header.ValidationHeader
import mx.edu.utez.jyps.ui.components.scanner.ManualCodeCard
import mx.edu.utez.jyps.ui.components.scanner.ScannerCard
import mx.edu.utez.jyps.viewmodel.ScannerStatus
import mx.edu.utez.jyps.viewmodel.ScannerUiState
import mx.edu.utez.jyps.viewmodel.ScannerViewModel

/**
 * Entry point for the Security Scanner screen.
 *
 * Acts as a thin ViewModel bridge, keeping the testable UI logic in [ScannerContent].
 * This wrapper is the only place where [ScannerViewModel] is instantiated via the
 * standard compose delegate.
 *
 * @param viewModel Injected ViewModel — defaulted for convenience from NavHost.
 * @param onLogoutClick Navigation callback for logout action.
 */
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel(),
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScannerContent(
        uiState = uiState,
        onManualCodeChange = { viewModel.onManualCodeChange(it) },
        onVerifyManualCode = { viewModel.verifyManualCode() },
        onQrCodeDetected = { viewModel.onQrCodeDetected(it) },
        onFrameWithQr = { viewModel.setQrInFrame(it) },
        onDismissResult = { viewModel.resetScanner() },
        onClearError = { viewModel.clearErrorToast() },
        onMockValidQR = { viewModel.mockValidQR() },
        onMockNoReturnQR = { viewModel.mockNoReturnQR() },
        onMockInvalidQR = { viewModel.mockInvalidQR() },
        onLogoutClick = onLogoutClick
    )
}

/**
 * Pure stateless UI for the Security Scanner.
 *
 * Separated from the ViewModel to enable @Preview and strict State Hoisting.
 * Mounts [ScanResultDialog] when [ScannerUiState.status] is not [ScannerStatus.Idle].
 *
 * @param uiState Full reactive state consumed by this screen.
 * @param onManualCodeChange Updates the manual code text field.
 * @param onVerifyManualCode Triggers verification of the manual code.
 * @param onQrCodeDetected Forwarded camera callback for decoded QR values.
 * @param onFrameWithQr Forwarded camera callback for overlay visibility.
 * @param onDismissResult Resets the scanner after a result dialog is closed.
 * @param onClearError Dismisses the transient error toast.
 * @param onMockValidQR Debug: simulates a PASE004 scan.
 * @param onMockNoReturnQR Debug: simulates a JUST001 scan.
 * @param onMockInvalidQR Debug: simulates an unknown code.
 * @param onLogoutClick Navigation callback.
 */
@Composable
fun ScannerContent(
    uiState: ScannerUiState,
    onManualCodeChange: (String) -> Unit,
    onVerifyManualCode: () -> Unit,
    onQrCodeDetected: (String) -> Unit,
    onFrameWithQr: (Boolean) -> Unit,
    onDismissResult: () -> Unit,
    onClearError: () -> Unit,
    onMockValidQR: () -> Unit,
    onMockNoReturnQR: () -> Unit,
    onMockInvalidQR: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // Scan result dialog — shown over everything when status is not Idle
    if (uiState.status !is ScannerStatus.Idle) {
        ScanResultDialog(
            status = uiState.status,
            onDismiss = onDismissResult
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            ValidationHeader(
                userName = "María González Hernández",
                onLogoutClick = onLogoutClick
            )

            // Fixed section: camera must NOT be inside a scrollable container.
            // Placing it outside prevents CameraX surface destruction on scroll events.
            ScannerCard(
                status = uiState.status,
                isQrInFrame = uiState.isQrInFrame,
                onQrDetected = onQrCodeDetected,
                onFrameWithQr = onFrameWithQr,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Scrollable section: only manual input and debug helpers scroll
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ManualCodeCard(
                    code = uiState.manualCode,
                    onCodeChange = onManualCodeChange,
                    onVerifyClick = onVerifyManualCode
                )

                // Debug helpers — only rendered in DEBUG builds to keep production clean
                if (BuildConfig.DEBUG) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onMockValidQR) { Text("QR Salida") }
                        TextButton(onClick = onMockNoReturnQR) { Text("QR Sin Regreso") }
                        TextButton(onClick = onMockInvalidQR) { Text("QR Inválido") }
                    }
                }
            }
        }

        AppToast(
            message = uiState.errorToast,
            isVisible = uiState.errorToast != null,
            onDismiss = onClearError,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ScannerScreenPreview() {
    ScannerContent(
        uiState = ScannerUiState(),
        onManualCodeChange = {},
        onVerifyManualCode = {},
        onQrCodeDetected = {},
        onFrameWithQr = {},
        onDismissResult = {},
        onClearError = {},
        onMockValidQR = {},
        onMockNoReturnQR = {},
        onMockInvalidQR = {},
        onLogoutClick = {}
    )
}
