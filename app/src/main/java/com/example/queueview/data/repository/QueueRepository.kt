package com.example.queueview.data.repository

import com.example.queueview.data.model.QueueData

interface QueueRepository {

    suspend fun addQueueData(queueData: QueueData): String
    suspend fun getAllQueues(): List<QueueData>

}