package com.example.queueview.presentation.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queueview.data.model.QueueData
import com.example.queueview.data.repository.QueueRepository
import kotlinx.coroutines.launch

class FormViewModel(
    private val repository: QueueRepository,
) : ViewModel() {


    fun submitQueueData(queueData: QueueData) {
        viewModelScope.launch {
            try {
                repository.addQueueData(queueData)
            } catch (e: Exception) {
                Log.e("FormViewModel", "Error submitting queue data", e)
            }
        }
    }
}