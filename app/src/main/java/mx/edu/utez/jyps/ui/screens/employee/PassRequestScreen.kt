package mx.edu.utez.jyps.ui.screens.employee

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.jyps.ui.theme.InfoBlue
import mx.edu.utez.jyps.ui.theme.InfoBlueBg
import mx.edu.utez.jyps.ui.theme.InfoBlueBorder
import mx.edu.utez.jyps.viewmodel.PassRequestViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.components.common.AppToast
import mx.edu.utez.jyps.ui.components.common.ToastType
import mx.edu.utez.jyps.ui.components.common.EmployeeModeBanner

/**
 * Screen that allows an employee to request a pass for leaving the establishment.
 *
 * @param viewModel The ViewModel managing the pass request state and logic.
 * @param onBackClick Callback that navigates to the previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassRequestScreen(
    viewModel: PassRequestViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSuccessSubmit: (String) -> Unit = {},
    showEmployeeModeBanner: Boolean = false,
    onReturnToRoleDashboard: () -> Unit = {},
    userName: String = "Juan",
    userEmail: String = "juan.perez@utez.edu.mx"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userName, userEmail) {
        viewModel.setUserInfo(userName, userEmail)
    }
    
    androidx.compose.runtime.LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onSuccessSubmit("Solicitud registrada con éxito.")
        }
    }
    
    if (uiState.showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { viewModel.onTimeDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onTimeSelected(
                        LocalTime.of(timePickerState.hour, timePickerState.minute)
                    )
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onTimeDismiss() }) { Text("Cancelar") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        topBar = {
            Column {
                // Header (App Name and User welcome)
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Sistema JyPS",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Bienvenido, $userName",
                                color = Color(0xFFE5E7EB),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Employee Mode banner — shown when accessed from DeptHead or Admin
                if (showEmployeeModeBanner) {
                    EmployeeModeBanner(onBackClick = onReturnToRoleDashboard)
                }
                
                // Back Button and Tittle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Solicitar Pase de Salida",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Personal Information (Read-only as per design)
                    AppTextField(
                        label = "Nombre Completo",
                        value = uiState.fullName,
                        onValueChange = {},
                        enabled = false
                    )
                    
                    AppTextField(
                        label = "Correo Institucional",
                        value = uiState.email,
                        onValueChange = {},
                        enabled = false
                    )

                    // Pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(Modifier.weight(1f)) {
                            AppTextField(
                                label = "Fecha de Salida",
                                value = uiState.dateDisplay,
                                onValueChange = {},
                                enabled = false,
                                trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF6A7282)) }
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            AppTextField(
                                label = "Hora de Salida",
                                value = uiState.timeDisplay,
                                onValueChange = {},
                                placeholder = "--:--",
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF6A7282)) },
                                onClick = { viewModel.onTimeClick() }
                            )
                        }
                    }

                    // Details Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Detalles del Pase *",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF364153)
                            )
                            Text(
                                "${uiState.details.length}/${uiState.detailsLimit}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (uiState.details.length !in uiState.detailsMinLimit..uiState.detailsLimit) Color.Red else Color(0xFF6A7282)
                            )
                        }
                        
                        OutlinedTextField(
                            value = uiState.details,
                            onValueChange = { viewModel.onDetailsChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { 
                                Text(
                                    "Describe el motivo de tu salida, destino y cualquier información relevante...",
                                    fontSize = 14.sp,
                                    color = Color(0x800A0A0A)
                                ) 
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFFD1D5DC)
                            )
                        )
                        
                        Text(
                            "Incluye el motivo, destino y hora estimada de regreso (si aplica)",
                            fontSize = 12.sp,
                            color = Color(0xFF6A7282)
                        )
                    }

                    // Action Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.7.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.Medium)
                        }
                        
                        Button(
                            onClick = { viewModel.onSubmit() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            enabled = uiState.isFormValid && !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Enviar Solicitud", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Info Note
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                color = InfoBlueBg,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, InfoBlueBorder)
            ) {
                Text(
                    text = "Nota: Tu solicitud será enviada para aprobación. Recibirás un código QR una vez que sea aprobada por un administrador.",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    color = InfoBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    AppToast(
        message = if (uiState.hasActivePassError) "Aún cuenta con una Solicitud de Pase de salida pendiente para el día de hoy" else uiState.error,
        isVisible = uiState.hasActivePassError || uiState.error != null,
        onDismiss = {
            if (uiState.hasActivePassError) {
                viewModel.clearActivePassError()
                onBackClick()
            } else {
                viewModel.clearError()
            }
        },
        type = ToastType.ERROR,
        modifier = Modifier.align(Alignment.BottomCenter)
    )

    }
}

@Preview(showSystemUi = true)
@Composable
fun PassRequestPreview() {
    PassRequestScreen()
}
