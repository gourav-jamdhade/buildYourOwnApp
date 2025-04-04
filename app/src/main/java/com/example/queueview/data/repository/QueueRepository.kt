package com.example.queueview.data.repository

import com.example.queueview.data.model.QueueData

interface QueueRepository {

    suspend fun addQueueData(queueData: QueueData): String
    suspend fun getAllQueues(): List<QueueData>
    suspend fun updateAllWaitTimes()
    suspend fun removeQueues(queueIds: List<String>)
    suspend fun findQueuesByLocation(lat: Double, lng: Double): List<QueueData>
    suspend fun findExistingAtLocation(lat: Double, lng: Double): QueueData?
    suspend fun updateWaitTime(queueId: String, newTime: Int)


}