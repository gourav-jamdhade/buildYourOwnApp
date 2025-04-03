package com.example.queueview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.queueview.navigation.NavGraph
import com.example.queueview.ui.theme.QueueViewTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    // Inside MainActivity (temporary test)

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Initialize OSMDroid config early
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContent {
            QueueViewTheme {
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    val viewModel: MainViewModel = koinViewModel()
//                    NearbyLocationScreen(
//                        viewModel = viewModel,
//                        onAddDataClick = {}
//                    )
//                }
                NavGraph() // This handles all navigation
            }
        }
    }
}

