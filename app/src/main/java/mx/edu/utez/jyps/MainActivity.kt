package mx.edu.utez.jyps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.ui.NavigationHost
import mx.edu.utez.jyps.ui.theme.JyPSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitInstance.init(this)
        enableEdgeToEdge()
        setContent {
            JyPSTheme {
                NavigationHost()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    JyPSTheme {
        NavigationHost()
    }
}