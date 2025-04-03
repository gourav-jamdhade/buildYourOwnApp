package com.example.queueview.presentation.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queueview.data.model.QueueData
import com.example.queueview.data.repository.QueueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val queueRepository: QueueRepository
) : ViewModel() {

    private val _queues = MutableStateFlow<List<QueueData>>(emptyList())
    val queues: StateFlow<List<QueueData>> = _queues

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAllQueues() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("Viewmodel fetch All Queues", "Fetching all queues")
                _queues.value = queueRepository.getAllQueues()
            } catch (e: Exception) {
                Log.e("Viewmodel fetch All Queues", "Error: ${e.message}")
            }

            _isLoading.value = false
        }
    }

    fun addQueue(queueData: QueueData) {
        viewModelScope.launch {

            try {
                queueRepository.addQueueData(queueData)
                fetchAllQueues() // Refresh the list
            } catch (e: Exception) {
                Log.e("Viewmodel add Queue", "Error: ${e.message}")
            }
        }
    }

}