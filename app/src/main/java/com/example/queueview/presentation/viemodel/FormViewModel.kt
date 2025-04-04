package com.example.queueview.presentation.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queueview.data.model.QueueData
import com.example.queueview.data.remote.LocationClient
import com.example.queueview.data.repository.QueueRepository
import com.example.queueview.utils.LocationPermissionException
import com.example.queueview.utils.exceptions.LocationTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class FormViewModel(
    private val repository: QueueRepository,
    private val locationClient: LocationClient

) : ViewModel()
{
    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    val currentLocation: StateFlow<GeoPoint?> = _currentLocation

    init {
        getLocationUpdates()
    }


    fun getLocationUpdates() {
        viewModelScope.launch {
            locationClient.getLocationUpdates()
                .catch { e ->
                    Log.e("FormViewModel", "Error getting location ${e.message}")
                }
                .collect { location ->
                    Log.d("FormViewModel", "Location received: $location")
                    _currentLocation.value = GeoPoint(location.latitude, location.longitude)
                }
        }
    }

    fun getCurrentLocation(): Flow<GeoPoint?> = callbackFlow {
        locationClient.getLocationUpdates().catch { e ->
            if (e is LocationTimeoutException) {
                Log.e("FormViewModel", "Location timeout error", e)
            }

            if (e is LocationPermissionException) {
                Log.e("FormViewModel", "Location permission error", e)
            } else {
                Log.e("FormViewModel", "Error getting location", e)
            }
        }.collect { androidLocation ->
            trySend(GeoPoint(androidLocation.latitude, androidLocation.longitude))
        }
    }


    sealed class SubmitResult {
        data class Success(val queueId: String) : SubmitResult()
        data class DuplicateUpdated(
            val existingName: String,
            val updatedAverageTime: Int  // Renamed for clarity
        ) : SubmitResult()

        data class Error(val message: String) : SubmitResult()
    }


    suspend fun handleSubmission(data: QueueData): SubmitResult {
        return try {
            // 1. Check for existing queues
            repository.findExistingAtLocation(data.latitude, data.longitude)?.let { existing ->

                Log.d("FormViewModel", "Time: ${existing.waitingTime}; ${data.waitingTime}")
                repository.updateWaitTime(existing.id, data.waitingTime)

                return SubmitResult.DuplicateUpdated(existing.placeName, data.waitingTime)
            } ?: run {
                // 2. If no duplicate, add new queue
                val newQueueId = repository.addQueueData(data)
                SubmitResult.Success(newQueueId)
            }
        } catch (e: Exception) {
            SubmitResult.Error("Submission failed: ${e.localizedMessage}")
        }
    }


}