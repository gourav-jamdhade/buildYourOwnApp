package com.example.queueview.data.remote

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.queueview.utils.LocationPermissionException
import com.example.queueview.utils.exceptions.LocationException
import com.example.queueview.utils.exceptions.LocationTimeoutException
import com.example.queueview.utils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) {
    fun getLocationUpdates(timeoutMillis: Long = 30000L): Flow<Location> = callbackFlow {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!context.hasLocationPermission()) {
            close(LocationPermissionException())
            return@callbackFlow
        }

        if (!hasFineLocation && !hasCoarseLocation) {
            throw LocationPermissionException()
        }


        val request = LocationRequest.create().apply {
            interval = 5000L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.lastOrNull()?.let { location ->
                    Log.d("LocationClient", "Location received: $location")
                    trySend(location)
                }
            }
        }

        // Timeout handler
        var timeoutJob: Job? = null
        fun cancelTimeout() {
            timeoutJob?.cancel()
        }

        timeoutJob = launch {
            delay(timeoutMillis)
            close(LocationTimeoutException())
        }

        try {
            client.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            ).addOnFailureListener { e ->
                close(e) // Close flow on failure
            }
        } catch (securityException: SecurityException) {
            close(securityException) // Handle cases where permission is revoked mid-operation
        }

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}