package com.example.queueview.presentation.components

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
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
    selectedLocation: GeoPoint? = null,
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
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    val selectionMarker = remember {
        Marker(mapView).apply {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_target_location)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
    }

    DisposableEffect(Unit) {
        val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                mapView.projection.fromPixels(e.x.toInt(), e.y.toInt())?.let { iGeoPoint ->
                    onLocationSelected(GeoPoint(iGeoPoint.latitude, iGeoPoint.longitude))
                }
                return true
            }
        })

        val overlay = object : Overlay() {
            override fun onTouchEvent(event: MotionEvent, mapView: MapView): Boolean {
                gestureDetector.onTouchEvent(event)
                return super.onTouchEvent(event, mapView)
            }
        }

        mapView.overlays.add(overlay)
        onDispose { mapView.overlays.remove(overlay) }
    }

    AndroidView(
        factory = { mapView },
        update = { view ->
            currentLocation?.let { view.controller.setCenter(it) }
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