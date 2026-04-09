package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

/**
 * A standard form input field.
 *
 * @param label The textual label above the field.
 * @param value The current string value.
 * @param onValueChange Callback to map typing.
 * @param placeholder Optional hint text.
 * @param errorMessage Message to show as an error description underneath.
 */
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    errorMessage: String? = null
) {
    val isError = errorMessage != null
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            placeholder = { Text(placeholder, color = Color(0x800A0A0A), fontSize = 16.sp) },
            shape = RoundedCornerShape(8.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F2C59),
                unfocusedBorderColor = if (isError) Color(0xFFDC3545) else Color(0xFFD1D5DC),
                errorBorderColor = Color(0xFFDC3545)
            ),
            singleLine = true
        )
        if (isError) {
            Text(
                text = errorMessage!!,
                color = Color(0xFFDC3545),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormFieldPreview() {
    FormField("Nombre", "Carlos", onValueChange = {}, placeholder = "Escribe tu nombre")
}
