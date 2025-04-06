package com.example.queueview.presentation.components

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.queueview.presentation.viemodel.SearchViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionWrapper(
    onPermissionGranted: (Location) -> Unit
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { onPermissionGranted(it) }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        when {
            permissionState.status.isGranted -> {
                Text("Location permission granted ✅")
            }

            permissionState.status.shouldShowRationale -> {
                Column {
                    Text("We need your location to show nearby results.")
                    Button(
                        onClick = { permissionState.launchPermissionRequest() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF14C18B)
                        )
                    ) {
                        Text("Grant Permission")
                    }
                }
            }

            else -> {
                Button(onClick = { permissionState.launchPermissionRequest() }, shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF14C18B)

                    )) {
                    Text("Request Location Permission")
                }
            }
        }
    }
}

@Composable
fun SearchWithLocationAwareSuggestions(viewModel: SearchViewModel) {
    var userLocation by remember { mutableStateOf<Location?>(null) }

    if (userLocation == null) {
        LocationPermissionWrapper { location ->
            userLocation = location
        }
    } else {
        NominatimSearchBar(viewModel = viewModel, userLocation = userLocation)
    }
}
