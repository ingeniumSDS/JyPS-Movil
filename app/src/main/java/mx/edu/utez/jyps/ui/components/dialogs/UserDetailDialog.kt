package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.ui.components.admin.UserDetailContent
import mx.edu.utez.jyps.viewmodel.AdminViewModel
import androidx.compose.ui.tooling.preview.Preview

/**
 * Detailed read-only view of a given user's information.
 * Lists schedule, roles, department, and current account access status.
 *
 * @param viewModel AdminViewModel providing the selected user data and account details.
 */
@Composable
fun UserDetailDialog(viewModel: AdminViewModel) {
    val isVisible by viewModel.isUserDetailVisible.collectAsStateWithLifecycle()
    val user by viewModel.selectedUser.collectAsStateWithLifecycle()
    val selectedAccount by viewModel.selectedUserAccount.collectAsStateWithLifecycle()

    if (isVisible && user != null) {
        Dialog(
            onDismissRequest = { viewModel.closeUserDetail() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detalle de Usuario", 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF0F2C59)
                        )
                        IconButton(onClick = { viewModel.closeUserDetail() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    
                    // Main Content: Combines user profile and security account info
                    UserDetailContent(
                        usuario = user!!, 
                        cuenta = selectedAccount
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailDialogPreview() {
    // Requires a mock user instance to be supplied via mocked ViewModel
}
