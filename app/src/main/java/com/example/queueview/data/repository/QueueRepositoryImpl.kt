package com.example.queueview.data.repository

import com.example.queueview.data.model.QueueData
import com.example.queueview.data.remote.FirebaseDataSource

class QueueRepositoryImpl(
    private val firebaseDataSource: FirebaseDataSource
) : QueueRepository {
    override suspend fun addQueueData(queueData: QueueData): String {
        return firebaseDataSource.addQueueData(queueData)
    }

    override suspend fun getAllQueues(): List<QueueData> {
        return firebaseDataSource.getAllQueues()
    }
}