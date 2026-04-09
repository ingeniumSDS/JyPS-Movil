package mx.edu.utez.jyps.ui.components.dialogs

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import mx.edu.utez.jyps.data.model.HistoryItem
import mx.edu.utez.jyps.data.model.EstadosIncidencia
import mx.edu.utez.jyps.ui.theme.PrimaryColor
import mx.edu.utez.jyps.utils.WalletTokenMockGenerator

/**
 * Reusable layout for displaying the QR Matrix and payload info for an Approved pass.
 * Includes CTAs for saving the pass to Google Wallet or the local gallery.
 *
 * @param item Record structure containing required properties like code and description.
 * @param onDismissRequest Triggered when the user wants to leave the viewer.
 * @param onDownloadMock Triggered to locally download the generated bitmap.
 */
@Composable
fun ApprovedPassQrDialog(
    item: HistoryItem,
    onDismissRequest: () -> Unit,
    onDownloadMock: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var rawBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(item.code) {
        // Generate QR code safely in a side-effect
        try {
            val size = 512
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(item.code, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
                }
            }
            qrBitmap = bitmap.asImageBitmap()
            rawBitmap = bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Código QR - Pase de Salida", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF64748B))
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                // The QR Image Wrapper
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, PrimaryColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    qrBitmap?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "Código QR de Pase ${item.code}",
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    } ?: CircularProgressIndicator()
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Code Identifier & Badge
                Text("Código de Pase", color = Color(0xFF64748B), fontSize = 12.sp)
                Text(item.code, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = PrimaryColor, letterSpacing = 2.sp)
                
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.background(Color(0xFFDCFCE7), RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("✅ Aprobado - Listo para usar", color = Color(0xFF166534), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Details Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fecha:", color = Color(0xFF64748B), fontSize = 13.sp)
                        Text(item.date, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1E293B))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hora de salida:", color = Color(0xFF64748B), fontSize = 13.sp)
                        Text(item.time, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1E293B))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Motivo:", color = Color(0xFF64748B), fontSize = 13.sp)
                        Text(
                            text = item.description, 
                            fontWeight = FontWeight.SemiBold, 
                            fontSize = 13.sp, 
                            color = Color(0xFF1E293B),
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alert Instructions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("📱 Instrucciones:", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 12.sp)
                        Text("Presenta este código QR o el código manual al personal de seguridad al salir de las instalaciones.", color = PrimaryColor, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add to Google Wallet CTA
                Button(
                    onClick = {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // Sign JWT on the fly
                                val fakeJwt = WalletTokenMockGenerator.generateSignedWalletToken(
                                    passCode = item.code,
                                    motive = item.description,
                                    employeeName = "Docente Administrativo UTEZ"
                                )
                                // Launch via Intent for Maximum Compatibility
                                val saveUri = "https://pay.google.com/gp/v/save/$fakeJwt"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(saveUri))
                                context.startActivity(intent)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir a la Cartera de Google", color = Color.White, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Download CTA
                OutlinedButton(
                    onClick = { rawBitmap?.let { onDownloadMock(it) } },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Descargar QR (Local)", color = PrimaryColor, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Close CTA
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Cerrar", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApprovedPassQrDialogPreview() {
    ApprovedPassQrDialog(
        item = HistoryItem("1", "Pase", EstadosIncidencia.APROBADO, "Salida Personal", "20/2/2026", "10:00", "PASE001"),
        onDismissRequest = {},
        onDownloadMock = {}
    )
}
