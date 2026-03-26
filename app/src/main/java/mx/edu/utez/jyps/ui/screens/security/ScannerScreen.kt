package mx.edu.utez.jyps.ui.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import mx.edu.utez.jyps.ui.components.header.ValidationHeader
import mx.edu.utez.jyps.ui.components.common.AppToast
import mx.edu.utez.jyps.ui.components.scanner.ManualCodeCard
import mx.edu.utez.jyps.ui.components.scanner.ScannerCard
import mx.edu.utez.jyps.ui.components.scanner.ScannerTabs
import mx.edu.utez.jyps.ui.components.scanner.ValidPassCard
import mx.edu.utez.jyps.viewmodel.ScannerStatus
import mx.edu.utez.jyps.viewmodel.ScannerTab
import mx.edu.utez.jyps.viewmodel.ScannerUiState
import mx.edu.utez.jyps.viewmodel.ScannerViewModel

/**
 * Entry point for the Security Scanner Screen.
 * 
 * This wrapper handles the ViewModel injection using the standard delegate,
 * keeping the core UI logic in [ScannerContent] for testability and previews.
 */
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    ScannerContent(
        uiState = uiState,
        onTabSelected = { viewModel.setTab(it) },
        onManualCodeChange = { viewModel.onManualCodeChange(it) },
        onVerifyManualCode = { viewModel.verifyManualCode() },
        onResetScanner = { viewModel.resetScanner() },
        onStartScan = { viewModel.startScanning() },
        onClearError = { viewModel.clearErrorToast() },
        onMockValidQR = { viewModel.mockValidQR() },
        onMockInvalidQR = { viewModel.mockInvalidQR() },
        onMockValidPass = { viewModel.mockValidPass() }
    )
}

/**
 * Pure UI content for the Security Scanner.
 * 
 * Separated from the ViewModel to allow Previewing with mock states and 
 * to follow the State Hoisting pattern strictly.
 */
@Composable
fun ScannerContent(
    uiState: ScannerUiState,
    onTabSelected: (ScannerTab) -> Unit,
    onManualCodeChange: (String) -> Unit,
    onVerifyManualCode: () -> Unit,
    onResetScanner: () -> Unit,
    onStartScan: () -> Unit,
    onClearError: () -> Unit,
    onMockValidQR: () -> Unit,
    onMockInvalidQR: () -> Unit,
    onMockValidPass: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            ValidationHeader(
                userName = "María González Hernández",
                onActionClick = { /* Drawer/menu logic */ }
            )

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ScannerTabs(
                    selectedTab = uiState.currentTab,
                    onTabSelected = onTabSelected
                )

                if (uiState.status is ScannerStatus.ValidPass) {
                    val passInfo = uiState.status as ScannerStatus.ValidPass
                    ValidPassCard(
                        name = passInfo.name,
                        email = passInfo.email,
                        date = passInfo.date,
                        code = passInfo.code,
                        type = passInfo.type,
                        onClose = onResetScanner
                    )
                } else {
                    if (uiState.currentTab == ScannerTab.QR) {
                        ScannerCard(
                            status = uiState.status,
                            onStartScan = onStartScan
                        )
                    } else {
                        ManualCodeCard(
                            code = uiState.manualCode,
                            onCodeChange = onManualCodeChange,
                            onVerifyClick = onVerifyManualCode
                        )
                    }

                    // QA & DEBUG MOCK SECTION
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onMockValidQR) { Text("Valid QR") }
                        TextButton(onClick = onMockInvalidQR) { Text("Invalid QR") }
                        TextButton(onClick = onMockValidPass) { Text("Full Pass") }
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
    // We use a mock state instead of a constructor call to avoid Lint issues
    ScannerContent(
        uiState = ScannerUiState(currentTab = ScannerTab.QR),
        onTabSelected = {},
        onManualCodeChange = {},
        onVerifyManualCode = {},
        onResetScanner = {},
        onStartScan = {},
        onClearError = {},
        onMockValidQR = {},
        onMockInvalidQR = {},
        onMockValidPass = {}
    )
}
