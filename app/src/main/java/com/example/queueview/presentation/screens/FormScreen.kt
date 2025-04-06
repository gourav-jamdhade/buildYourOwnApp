package com.example.queueview.presentation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queueview.data.model.QueueData
import com.example.queueview.presentation.components.MapContainer
import com.example.queueview.presentation.components.ToastHost
import com.example.queueview.presentation.components.ToastHostState
import com.example.queueview.presentation.viemodel.FormViewModel
import com.example.queueview.utils.FontUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FormScreen(
    viewModel: FormViewModel, onBackClick: () -> Unit // Callback to navigate back
) {

    // Permission state
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val toastHostState = remember { ToastHostState() }

    // Handle permission and location updates
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            viewModel.getCurrentLocation()
            viewModel.getLocationUpdates()
        }
    }

    val currentLocation by viewModel.currentLocation.collectAsState()
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val displayLocation = selectedLocation ?: currentLocation
    var placeName by remember { mutableStateOf("") }
    var placeType by remember { mutableStateOf("BANK") }
    var waitingTime by remember { mutableStateOf("") }
//    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = ("Add New Place"),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 8.dp),
                        fontFamily = FontUtils().getFontFamily(),
                    )
                }

            },
            // colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF5F33E1)),
            modifier = Modifier
                .height(38.dp)
                .background(Color.White),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)


        )
    }) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Map placeholder (30% height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray), contentAlignment = Alignment.Center

            ) {

                when {
                    // Permission not granted case
                    !locationPermissionState.status.isGranted -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Location permission required",
                                style = TextStyle(
                                    fontFamily = FontUtils().getFontFamily(),
                                    fontSize = 16.sp
                                ),
                                fontWeight = FontWeight.Light,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF14C18B)
                                ),
                                onClick = {
                                    selectedLocation = null
                                    locationPermissionState.launchPermissionRequest()
                                }) {
                                Text("Enable Location")
                            }
                        }
                    }


                    // Loading case
                    displayLocation == null -> {
                        CircularProgressIndicator(
                            color = Color(0xFF14C18B),
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 3.dp
                        )
                    }

                    else -> {
                        currentLocation?.let {
                            MapContainer(
                                currentLocation = displayLocation,
                                selectedLocation = selectedLocation ?: currentLocation,
                                onLocationSelected = { point ->
                                    selectedLocation = point
                                }
                            )
                        }
                    }
                }


            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display coordinates somewhere in your form
            Row(
            ) {
                if (displayLocation != null) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontUtils().getFontFamily()
                                )
                            ) {
                                append("Coordinates:")
                            }
                            append(
                                " ${"%.4f".format(displayLocation.latitude)}, " +
                                        "${"%.4f".format(displayLocation.longitude)}"
                            )
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    if (locationPermissionState.status.isGranted) {
                        CircularProgressIndicator(
                            color = Color(0xFF14C18B),
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text("Location not available", modifier = Modifier.padding(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Places Name Field
            OutlinedTextField(
                value = placeName,
                onValueChange = { placeName = it },
                label = { Text("Place Name *") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF14C18B),
                    focusedLabelColor = Color(0xFF14C18B),
                    unfocusedIndicatorColor = Color.Black,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontFamily = FontUtils().getFontFamily(),
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

//Places Type DD
            var expanded by remember { mutableStateOf(false) }
            val placeTypes = listOf(
                "BANK",
                "RESTAURANT",
                "HOSPITAL",
                "SALON",
                "GARAGE",
                "MEDICAL",
                "GROCERY STORE",
                "OTHER"
            )
            Column(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(
                    width = 1.dp,
                    color = androidx.compose.ui.graphics.Color.Black,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable { expanded = true }) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = placeType)
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                    )
                }
                Log.d("DropDown", "$expanded")
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)

                ) {
                    placeTypes.forEach { type ->
                        DropdownMenuItem(text = { Text(text = type) }, onClick = {
                            placeType = type
                            expanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

//Time Field
            OutlinedTextField(
                value = waitingTime,
                onValueChange = { waitingTime = it },
                label = { Text("Waiting Time (mins) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF14C18B),
                    focusedLabelColor = Color(0xFF14C18B),
                    unfocusedIndicatorColor = Color.Black,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                textStyle = TextStyle(
                    fontFamily = FontUtils().getFontFamily(),
                )

            )


            //Submit Button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF14C18B)
                ),
                onClick = {

                    displayLocation?.let { location ->
                        if (placeName.isNotBlank() && waitingTime.isNotBlank()) {
                            scope.launch {
                                when (val result = viewModel.handleSubmission(
                                    QueueData(
                                        placeName = placeName,
                                        placeType = placeType,
                                        waitingTime = waitingTime.toInt(),
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )
                                )) {
                                    is FormViewModel.SubmitResult.Success -> {
                                        toastHostState.showToast("Added successfully!")
                                        onBackClick()
                                    }

                                    is FormViewModel.SubmitResult.DuplicateUpdated -> {

                                        toastHostState.showToast("Updated '${result.existingName}' to $waitingTime min")
                                        onBackClick()
                                    }

                                    is FormViewModel.SubmitResult.Error -> {
                                        toastHostState.showToast(result.message)
                                        Log.e("FormScreen", result.message)

                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = placeName.isNotBlank() && waitingTime.isNotBlank() && displayLocation != null
            ) {
                Text("Submit")
            }

        }
        ToastHost(toastHostState)

    }
}