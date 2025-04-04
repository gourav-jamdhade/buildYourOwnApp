package com.example.queueview.presentation.components

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.queueview.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import kotlin.math.abs

@Composable
fun MapContainer(
    modifier: Modifier = Modifier,
    currentLocation: GeoPoint?,
    selectedLocation: GeoPoint? = null, // Null means no selection
    onLocationSelected: (GeoPoint) -> Unit = {}

) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            minZoomLevel = 4.0
            maxZoomLevel = 19.0
            controller.setZoom(15.0)
        }
    }


    // Single marker instance
    val selectionMarker = remember {
        Marker(mapView).apply {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_target_location)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    DisposableEffect(Unit) {
        // Create a tap overlay that properly respects gestures
        val tapOverlay = object : Overlay() {
            private var startX = 0f
            private var startY = 0f
            private val touchTolerance = 15f // Pixels of movement to consider a drag vs tap
            private var isDragging = false

            override fun onTouchEvent(event: MotionEvent, mapView: MapView): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x
                        startY = event.y
                        isDragging = false
                        // Don't consume DOWN events - let them pass through
                        return false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        // Calculate distance moved
                        val dx = abs(event.x - startX)
                        val dy = abs(event.y - startY)

                        // If moved more than tolerance, it's a drag
                        if (!isDragging && (dx > touchTolerance || dy > touchTolerance)) {
                            isDragging = true
                        }

                        // Don't consume MOVE events
                        return false
                    }

                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            // It was a tap, not a drag
                            mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
                                ?.let { iGeoPoint ->
                                    onLocationSelected(
                                        GeoPoint(
                                            iGeoPoint.latitude,
                                            iGeoPoint.longitude
                                        )
                                    )
                                }
                            return true // Consume this UP event
                        }
                        return false // Let pan gesture complete
                    }
                }
                return false
            }
        }

        // Make sure this overlay is added AFTER any default map gestures
        mapView.overlays.add(tapOverlay)

        onDispose {
            mapView.overlays.remove(tapOverlay)
        }
    }


    AndroidView(
        factory = { mapView },
        update = { view ->
            view.controller.setCenter(currentLocation)
            selectedLocation?.let {
                selectionMarker.position = it
                if (selectionMarker !in view.overlays) {
                    view.overlays.add(selectionMarker)
                }
            } ?: run {
                view.overlays.remove(selectionMarker)
            }
        },
        modifier = modifier.clipToBounds()
    )
}