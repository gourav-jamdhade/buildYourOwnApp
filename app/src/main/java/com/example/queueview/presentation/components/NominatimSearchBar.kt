package com.example.queueview.presentation.components

import android.location.Location
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.queueview.presentation.viemodel.SearchViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun NominatimSearchBar(viewModel: SearchViewModel, userLocation: Location?) {

    var query by remember { mutableStateOf("") }
    val suggestions by viewModel.results.collectAsState()

    var expanded by remember { mutableStateOf(false) }


    // Debounce the query input
    LaunchedEffect(query) {

        Log.d("SearchViewModel", "${userLocation?.latitude}, ${userLocation?.longitude}")
        viewModel.search(
            query,
            location = userLocation
        )
        snapshotFlow { query }
            .debounce(400)
            .collectLatest {
                viewModel.search(
                    it,
                    location = userLocation
                )
                Log.d("SearchViewModel", "Query: $suggestions")
                expanded = true
            }


    }

    Column(modifier = Modifier.padding(16.dp)) {

        ScrollableSingleLineTextField(
            value = query,
            onValueChange = { query = it },
            label = "Search Location",
            modifier = Modifier
                .fillMaxWidth()

        )

        AnimatedVisibility(
            visible = expanded && suggestions.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                items(suggestions) { item ->
                    Text(
                        text = item.display_name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable {
                                query = item.display_name
                                expanded = false
                            }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}
