package com.example.queueview.presentation.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queueview.data.model.ObservableQueueData
import com.example.queueview.data.model.QueueData
import com.example.queueview.data.repository.QueueRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val queueRepository: QueueRepository
) : ViewModel() {

    // Add a manual refresh  function
    fun forceRefresh() {
        viewModelScope.launch {
            fetchAllQueues()
        }
    }

    private val _queues = MutableStateFlow<List<ObservableQueueData>>(emptyList())
    val queues: StateFlow<List<ObservableQueueData>> = _queues

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(60000) // 1 minute
                updateTimesLocally() // Only update local state
            }
        }
    }

    private fun updateTimesLocally() {
        _queues.value = _queues.value.map { observable ->
            observable.updateTime()
            observable
        }
    }

    fun removeQueue(queueId: String) {
        viewModelScope.launch {
            // Update local state immediately
            _queues.value = _queues.value.filterNot { it.queueData.id == queueId }

            // Sync with Firestore
            try {
                queueRepository.removeQueues(listOf(queueId))
            } catch (e: Exception) {
                // Re-add if Firestore fails
                _queues.value = queueRepository.getAllQueues().map { ObservableQueueData(it) }
            }
        }
    }


    fun fetchAllQueues() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("Viewmodel fetch All Queues", "Fetching all queues")
                _queues.value = queueRepository.getAllQueues().map { ObservableQueueData(it) }
            } catch (e: Exception) {
                Log.e("Viewmodel fetch All Queues", "Error: ${e.message}")
            } finally {

                _isLoading.value = false
            }


        }
    }

    val dummyQueueList = listOf(
        QueueData(
            id = "1",
            placeName = "HDFC Bank - MG Road",
            placeType = "BANK",
            latitude = 28.6139,
            longitude = 77.2090,
            waitingTime = 15,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_001",
            wasAveraged = false
        ),
        QueueData(
            id = "2",
            placeName = "Apollo Hospital",
            placeType = "HOSPITAL",
            latitude = 28.5672,
            longitude = 77.2100,
            waitingTime = 45,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_002",
            wasAveraged = true
        ),
        QueueData(
            id = "3",
            placeName = "Domino's Pizza - Sector 18",
            placeType = "RESTAURANT",
            latitude = 28.5700,
            longitude = 77.3260,
            waitingTime = 10,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_003",
            wasAveraged = false
        ),
        QueueData(
            id = "4",
            placeName = "ICICI Bank - Connaught Place",
            placeType = "BANK",
            latitude = 28.6304,
            longitude = 77.2177,
            waitingTime = 25,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_004",
            wasAveraged = true
        ),
        QueueData(
            id = "5",
            placeName = "Max Super Specialty Hospital",
            placeType = "HOSPITAL",
            latitude = 28.5535,
            longitude = 77.2078,
            waitingTime = 35,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_005",
            wasAveraged = false
        ),
        QueueData(
            id = "6",
            placeName = "Barbeque Nation - Noida",
            placeType = "RESTAURANT",
            latitude = 28.5701,
            longitude = 77.3265,
            waitingTime = 20,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_006",
            wasAveraged = true
        ), QueueData(
            id = "7",
            placeName = "Axis Bank - Nehru Place",
            placeType = "BANK",
            latitude = 28.5494,
            longitude = 77.2510,
            waitingTime = 18,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_007",
            wasAveraged = false
        ),
        QueueData(
            id = "8",
            placeName = "AIIMS Emergency Wing",
            placeType = "HOSPITAL",
            latitude = 28.5673,
            longitude = 77.2109,
            waitingTime = 60,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_008",
            wasAveraged = true
        ),
        QueueData(
            id = "9",
            placeName = "Burger King - Rajouri Garden",
            placeType = "RESTAURANT",
            latitude = 28.6448,
            longitude = 77.1123,
            waitingTime = 12,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_009",
            wasAveraged = false
        ),
        QueueData(
            id = "10",
            placeName = "PNB - Karol Bagh",
            placeType = "BANK",
            latitude = 28.6517,
            longitude = 77.1910,
            waitingTime = 22,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_010",
            wasAveraged = true
        ),
        QueueData(
            id = "11",
            placeName = "Fortis Hospital - Vasant Kunj",
            placeType = "HOSPITAL",
            latitude = 28.5262,
            longitude = 77.1535,
            waitingTime = 50,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_011",
            wasAveraged = false
        ),
        QueueData(
            id = "12",
            placeName = "KFC - Connaught Place",
            placeType = "RESTAURANT",
            latitude = 28.6328,
            longitude = 77.2197,
            waitingTime = 8,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_012",
            wasAveraged = false
        ),
        QueueData(
            id = "13",
            placeName = "SBI - Saket",
            placeType = "BANK",
            latitude = 28.5286,
            longitude = 77.2206,
            waitingTime = 30,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_013",
            wasAveraged = true
        ),
        QueueData(
            id = "14",
            placeName = "Safdarjung Hospital",
            placeType = "HOSPITAL",
            latitude = 28.5672,
            longitude = 77.2073,
            waitingTime = 40,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_014",
            wasAveraged = false
        ),
        QueueData(
            id = "15",
            placeName = "McDonald's - Janpath",
            placeType = "RESTAURANT",
            latitude = 28.6292,
            longitude = 77.2166,
            waitingTime = 14,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_015",
            wasAveraged = true
        ),
        QueueData(
            id = "16",
            placeName = "Canara Bank - Lajpat Nagar",
            placeType = "BANK",
            latitude = 28.5670,
            longitude = 77.2430,
            waitingTime = 27,
            lastUpdated = Timestamp.now(),
            submittedBy = "user_016",
            wasAveraged = false
        )
    )



}