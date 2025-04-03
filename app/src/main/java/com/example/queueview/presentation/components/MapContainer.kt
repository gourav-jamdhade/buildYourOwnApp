package com.example.queueview.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.queueview.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapContainer(
    modifier: Modifier = Modifier,
    currentLocation: GeoPoint = GeoPoint(0.0, 0.0) // Default to null island

) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            minZoomLevel = 4.0
            maxZoomLevel = 19.0
            controller.setZoom(15.0)
            // 3. Hardware acceleration
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            setUseDataConnection(true)
            setTilesScaledToDpi(true) // Better rendering
        }
    }

    // Track if initial location is set
    var isInitialLocationSet by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        // Required OSMDroid configuration
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = context.cacheDir
            tileDownloadThreads = 2  // Optimal for most devices
            tileDownloadMaxQueueSize = 4
        }

        onDispose { }
    }
    AndroidView(
        factory = { mapView },
        update = { view ->
            // Clear existing markers
            view.overlays.clear()

            // Add current location marker
            Marker(view).apply {
                position = currentLocation
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(context, R.drawable.ic_current_location)
                view.overlays.add(this)
            }

            view.controller.setCenter(currentLocation)
        },
        modifier = modifier.clipToBounds()
    )
}