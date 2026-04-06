package mx.edu.utez.jyps.ui.screens.employee

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mx.edu.utez.jyps.ui.components.common.AppToast
import mx.edu.utez.jyps.ui.components.common.ToastType
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.components.common.EmployeeModeBanner
import mx.edu.utez.jyps.viewmodel.JustificationRequestViewModel
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Screen that allows an employee to request a justification (Justificante) for an absence.
 *
 * @param viewModel The ViewModel managing the state.
 * @param onBackClick Callback to navigate back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JustificationRequestScreen(
    viewModel: JustificationRequestViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSuccessSubmit: (String) -> Unit = {},
    showEmployeeModeBanner: Boolean = false,
    onReturnToRoleDashboard: () -> Unit = {},
    userName: String = "Juan",
    userEmail: String = "juan.perez@utez.edu.mx"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(userName, userEmail) {
        viewModel.setUserInfo(userName, userEmail)
    }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var localValidationToast by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onSuccessSubmit("Solicitud registrada con éxito.")
        }
    }

    // Launchers for Gallery and Camera
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> if (uri != null) viewModel.onFileAttached(context, uri) }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) viewModel.onFileAttached(context, cameraImageUri) }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val newUri = createImageUri(context)
                cameraImageUri = newUri
                cameraLauncher.launch(newUri)
            }
        }
    )



    if (uiState.showDatePicker) {
        val todayMillis = System.currentTimeMillis()
        val selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                val today = LocalDate.now()
                val limit = today.minusDays(3)
                return !date.isAfter(today) && !date.isBefore(limit)
            }
        }
        val datePickerState = rememberDatePickerState(selectableDates = selectableDates)
        
        DatePickerDialog(
            onDismissRequest = { viewModel.onDateDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.onDateSelected(date)
                    }
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDateDismiss() }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
        topBar = {
            Column {
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
                                "Bienvenido, $userName", // Dummy name corresponding to dummy data
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
                        "Solicitar Justificante",
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

                    Column {
                        AppTextField(
                            label = "Fecha de Inasistencia",
                            value = uiState.dateDisplay,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = "Seleccionar...",
                            trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF6A7282)) },
                            onClick = { viewModel.onDateClick() }
                        )
                        Text(
                            text = "Solo puedes solicitar justificantes hasta 3 días hábiles previos a la fecha actual",
                            fontSize = 12.sp,
                            color = Color(0xFF6A7282),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Detalles del Justificante *",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF364153)
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
                                    "Describe el motivo de tu inasistencia con detalle...",
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
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Proporciona la mayor cantidad de detalles posible para agilizar la aprobación",
                                fontSize = 12.sp,
                                color = Color(0xFF6A7282),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${uiState.details.length}/${uiState.detailsLimit}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (uiState.details.length !in uiState.detailsMinLimit..uiState.detailsLimit) Color.Red else Color(0xFF6A7282),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Archivos Adjuntos (Recomendado)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF364153)
                            )
                            Text(
                                "${uiState.attachedUris.size}/${uiState.maxFiles} Archivos",
                                fontSize = 12.sp,
                                color = Color(0xFF6A7282)
                            )
                        }
                        
                        if (uiState.attachedUris.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.attachedUris.forEach { uri ->
                                    val mimeType = context.contentResolver.getType(uri) ?: ""
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF3F4F6),
                                        border = BorderStroke(1.dp, Color(0xFFD1D5DC))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (mimeType.contains("pdf")) {
                                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color(0xFFDB4437))
                                            } else {
                                                AsyncImage(
                                                    model = uri,
                                                    contentDescription = "Evidencia",
                                                    modifier = Modifier.size(36.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            val fileName = getFileName(context, uri)
                                            Text(
                                                text = fileName,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        try {
                                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                                setDataAndType(uri, mimeType)
                                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                            }
                                                            context.startActivity(Intent.createChooser(intent, "Ver archivo"))
                                                        } catch (e: Exception) {
                                                            localValidationToast = "No hay apps para abrir este archivo."
                                                        }
                                                    },
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                textDecoration = TextDecoration.Underline,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            IconButton(onClick = { viewModel.removeFile(uri) }) {
                                                Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color(0xFF6A7282))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { 
                                        if (uiState.attachedUris.size >= uiState.maxFiles) {
                                            localValidationToast = "Has alcanzado el límite de ${uiState.maxFiles} archivos."
                                        } else {
                                            galleryLauncher.launch(arrayOf("image/jpeg", "image/png", "application/pdf"))
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFD1D5DC)),
                                color = if (uiState.attachedUris.size < uiState.maxFiles) Color.Transparent else Color(0xFFF8F9FA)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = Color(0xFF6A7282))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Adjuntar evidencia", fontSize = 14.sp, color = Color(0xFF4A5565))
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { 
                                        if (uiState.attachedUris.size >= uiState.maxFiles) {
                                            localValidationToast = "Has alcanzado el límite de ${uiState.maxFiles} archivos."
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA) 
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFD1D5DC)),
                                color = if (uiState.attachedUris.size < uiState.maxFiles) Color.Transparent else Color(0xFFF8F9FA)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Cámara", tint = Color(0xFF6A7282))
                                }
                            }
                        }
                        
                        Text(
                            "Receta médica, comprobante de cita, carta de padres, etc.",
                            fontSize = 12.sp,
                            color = Color(0xFF6A7282)
                        )
                    }

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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)) // Exact Green from Figma
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

            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFB9F8CF))
            ) {
                Text(
                    text = "Tip: Adjuntar evidencia (receta médica, comprobante, etc.) acelera significativamente el proceso de aprobación.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF016630)
                )
            }
        }
    }

    AppToast(
        message = uiState.error,
        isVisible = uiState.error != null,
        onDismiss = { viewModel.clearError() },
        type = ToastType.ERROR,
        modifier = Modifier.align(Alignment.BottomCenter)
    )

    AppToast(
        message = localValidationToast,
        isVisible = localValidationToast != null,
        onDismiss = { localValidationToast = null },
        type = ToastType.INFO,
        modifier = Modifier.align(Alignment.BottomCenter)
    )
    }
}

/**
 * Utility function to create a dummy Uri file targeting the App Cache.
 * Used internally by camera logic.
 */
private fun createImageUri(context: Context): Uri {
    val imageDir = File(context.cacheDir, "images")
    imageDir.mkdirs()
    val file = File(imageDir, "justificante_evidencia_${System.currentTimeMillis()}.jpg")
    val authority = "${context.packageName}.provider"
    return FileProvider.getUriForFile(context, authority, file)
}

/**
 * Utility function to extract real file name from a Uri.
 */
private fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = cursor.getString(index)
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) result = result?.substring(cut + 1)
    }
    return result ?: "Archivo adjunto"
}

@Preview(showSystemUi = true)
@Composable
fun JustificationRequestPreview() {
    JustificationRequestScreen()
}
