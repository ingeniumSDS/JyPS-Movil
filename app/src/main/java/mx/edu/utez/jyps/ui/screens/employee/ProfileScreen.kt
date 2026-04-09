package mx.edu.utez.jyps.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.jyps.ui.components.navigation.AppBottomNavigationBar
import mx.edu.utez.jyps.ui.components.header.EmployeeHeader
import mx.edu.utez.jyps.ui.components.rows.InfoRow
import mx.edu.utez.jyps.ui.components.dialogs.ChangePasswordDialog
import mx.edu.utez.jyps.ui.components.common.AppToast
import mx.edu.utez.jyps.ui.components.common.ToastType
import mx.edu.utez.jyps.ui.components.common.EmployeeModeBanner
import mx.edu.utez.jyps.viewmodel.ProfileViewModel

/**
 * Screen displaying the employee's personal profile information and settings.
 * 
 * @param viewModel ViewModel with profile state logic.
 * @param onLogoutClick Callback to handle session termination.
 * @param onHomeClick Navigation to home dashboard.
 * @param onHistoryClick Navigation to history screen.
 * @param showEmployeeModeBanner Flag to display the contextual mode banner.
 * @param onReturnToRoleDashboard Callback to close the employee context.
 * @param userName Current session user's full name.
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogoutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    showEmployeeModeBanner: Boolean = false,
    onReturnToRoleDashboard: () -> Unit = {},
    userName: String = "Empleado"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismissRequest = { viewModel.dismissChangePassword() },
            onSave = { current, new, confirm -> viewModel.updatePassword(current, new, confirm) }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        topBar = {
            EmployeeHeader(
                userName = userName.split(" ").firstOrNull() ?: "",
                onLogoutClick = onLogoutClick
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                selectedRoute = "perfil",
                onHomeClick = onHomeClick,
                onHistoryClick = onHistoryClick
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Employee Mode banner — shown when accessed from DeptHead or Admin
            if (showEmployeeModeBanner) {
                EmployeeModeBanner(onBackClick = onReturnToRoleDashboard)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Mi Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Información personal",
                    fontSize = 16.sp,
                    color = Color(0xFF4A5565)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Profile Avatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            Color(16, 46, 92),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = userName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = uiState.role,
                                fontSize = 14.sp,
                                color = Color(0xFF4A5565),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Contact Info Sections
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoRow(
                            label = "Correo Institucional",
                            value = uiState.email,
                            icon = Icons.Outlined.Email
                        )
                        InfoRow(
                            label = "Teléfono",
                            value = uiState.phone,
                            icon = Icons.Outlined.Phone
                        )
                    }

                    // Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.changePassword() },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.7.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Cambiar Contraseña", fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = onLogoutClick,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
        
    AppToast(
        message = uiState.passwordOpMessage,
        isVisible = uiState.passwordOpMessage != null,
        onDismiss = { viewModel.clearOpMessage() },
        type = if (uiState.isPasswordOpSuccess) ToastType.SUCCESS else ToastType.ERROR,
        modifier = Modifier.align(Alignment.BottomCenter)
    )
}
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
