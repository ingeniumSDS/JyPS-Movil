package mx.edu.utez.jyps.ui.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import mx.edu.utez.jyps.viewmodel.ScannerViewModel

/**
 * Main Security Scanner Screen.
 *
 * Follows Clean Architecture (UI driven by model states).
 * Observes the ViewModel state using `collectAsStateWithLifecycle()` as per standards.
 * 
 * Dependencies are distributed by harboring only top-level components and general display logic,
 * favoring Maintainability metrics in case the scanner box camera needs to be replaced by a CameraX view,
 * without breaking any data validation flows.
 *
 * @param viewModel ViewModel instance connected to derive and expose the data flow down to its UI children.
 */
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel()
) {
    // Listens to status StateFlow and triggers recompositions respecting NavGraph lifecycle
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)) // General App background
        ) {
            // Header (Unchanged for all states of this particular screen)
            ValidationHeader(
                userName = "María González Hernández",
                onActionClick = { /* Logic pending for drawer/menu or extra options */ }
            )

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // Tab Selector (QR or Keyboard)
                ScannerTabs(
                    selectedTab = uiState.currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )

                // General view switcher based on state validity
                if (uiState.status is ScannerStatus.ValidPass) {
                    // Instance and mapping of a validated pass to its display card
                    val passInfo = uiState.status as ScannerStatus.ValidPass
                    ValidPassCard(
                        name = passInfo.name,
                        email = passInfo.email,
                        date = passInfo.date,
                        code = passInfo.code,
                        type = passInfo.type,
                        onClose = { viewModel.resetScanner() }
                    )
                } else {
                    // Toggle between visual scanner and manual input card
                    if (uiState.currentTab == ScannerTab.QR) {
                        ScannerCard(
                            status = uiState.status,
                            onStartScan = {
                                viewModel.startScanning()
                            }
                        )
                    } else {
                        ManualCodeCard(
                            code = uiState.manualCode,
                            onCodeChange = { viewModel.onManualCodeChange(it) },
                            onVerifyClick = { viewModel.verifyManualCode() }
                        )
                    }

                    // ----------------------------------------------------
                    // QA & DEBUG MOCK SECTION:
                    // Temporary manual controls to preview expected feedbacks 
                    // since no real physical reader is connected yet.
                    // ----------------------------------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { viewModel.mockValidQR() }) { Text("Valid QR") }
                        TextButton(onClick = { viewModel.mockInvalidQR() }) { Text("Invalid QR") }
                        TextButton(onClick = { viewModel.mockValidPass() }) { Text("Full Pass") }
                    }
                }
            }
        }

        // Overlay for Toast notification feedback at the bottom
        AppToast(
            message = uiState.errorToast,
            isVisible = uiState.errorToast != null,
            onDismiss = { viewModel.clearErrorToast() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ScannerScreenPreview() {
    ScannerScreen(viewModel = ScannerViewModel())
}
