package com.example.queueview.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.queueview.data.model.QueueData
import com.example.queueview.presentation.components.MapContainer
import com.example.queueview.presentation.viemodel.FormViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FormScreen(
    viewModel: FormViewModel, onBackClick: () -> Unit // Callback to navigate back
) {
    val dummyLocation = remember { GeoPoint(37.7749, -122.4194) } // San Francisco as example

    var placeName by remember { mutableStateOf("") }
    var placeType by remember { mutableStateOf("BANK") }
    var waitingTime by remember { mutableStateOf("") }

    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = ("Add Queue Data")) }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        })
    }) { padding ->


        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Map placeholder (30% height)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray)
            ) {
                MapContainer(
                    currentLocation = dummyLocation // Temporary static location
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            //Places Name Field
            OutlinedTextField(
                value = placeName,
                onValueChange = { placeName = it },
                label = { Text("Place Name *") },
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()

            )

//Submit Button

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (placeName.isNotBlank() && waitingTime.isNotBlank()) {
                        viewModel.submitQueueData(
                            QueueData(
                                placeName = placeName,
                                placeType = placeType,
                                waitingTime = waitingTime.toInt(),
                                latitude = 0.0,  // TODO: Add real location later
                                longitude = 0.0
                            )
                        )

                        onBackClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = placeName.isNotBlank() && waitingTime.isNotBlank()
            ) {
                Text("Submit")
            }

        }
    }
}