package com.example.queueview.presentation.viemodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queueview.data.model.NominatimResult
import com.example.queueview.data.remote.NominatimClient
import com.example.queueview.data.remote.createViewBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _results = MutableStateFlow<List<NominatimResult>>(emptyList())
    val results: StateFlow<List<NominatimResult>> = _results

    fun search(query: String, location: Location?) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (query.isNotBlank()) {

                    val viewbox = location?.let {
                        createViewBox(it.latitude, it.longitude)
                    }
                    Log.d("SearchViewModel", "Query: $query")
                    val res = NominatimClient.instance.search(
                        query = query,
                        lat = location?.latitude,
                        lon = location?.longitude,
                        viewbox = viewbox,
                        bounded = 1
                    )
                    Log.d("SearchViewModel", "Response: $res")
                    _results.value = res
                } else {
                    Log.d("SearchViewModel", "Query is blank")
                    _results.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error: ${e.message}")
                _results.value = emptyList()
            }
        }
    }
}



