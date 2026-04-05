package mx.edu.utez.jyps.ui.components.inputs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * AppTextField is a reusable input component that follows Figma design.
 * 
 * @param value The current text value.
 * @param onValueChange The callback when text changes.
 * @param label The label text above the input.
 * @param placeholder The placeholder text inside the input.
 * @param modifier The modifier to be applied.
 * @param isPassword Whether this is a password field.
 * @param enabled Whether the text field is enabled.
 * @param readOnly Whether the text field is read-only (can't be edited but can be clicked/focused).
 * @param trailingIcon A composable to show at the end of the input (overrides password eye).
 * @param onClick Optional callback when the field is clicked (useful for pickers over readOnly fields).
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                readOnly = readOnly,
                placeholder = { 
                    Text(
                        text = placeholder,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    ) 
                },
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = trailingIcon ?: if (isPassword) {
                    {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                } else null,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = Color(0xFFF3F4F6),
                    disabledBorderColor = Color(0xFFD1D5DC),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTrailingIconColor = Color(0xFF6A7282)
                ),
                singleLine = true
            )
            
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppTextFieldPreview() {
    JyPSTheme {
        AppTextField(
            value = "",
            onValueChange = {},
            label = "Correo Electrónico",
            placeholder = "tu@email.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppPasswordFieldPreview() {
    JyPSTheme {
        AppTextField(
            value = "password123",
            onValueChange = {},
            label = "Contraseña",
            placeholder = "••••••••",
            isPassword = true
        )
    }
}
