package com.example.queueview.presentation.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queueview.data.model.ObservableQueueData
import com.example.queueview.data.repository.QueueRepository
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


}