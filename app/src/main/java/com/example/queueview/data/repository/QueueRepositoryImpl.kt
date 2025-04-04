package com.example.queueview.data.repository

import android.util.Log
import com.example.queueview.data.model.QueueData
import com.example.queueview.data.remote.FirebaseDataSource
import kotlinx.coroutines.tasks.await

class QueueRepositoryImpl(
    private val firebaseDataSource: FirebaseDataSource
) : QueueRepository {
    override suspend fun addQueueData(queueData: QueueData): String {
        return firebaseDataSource.addQueueData(queueData)
    }

    override suspend fun getAllQueues(): List<QueueData> {
        return firebaseDataSource.getAllQueues()
    }

    override suspend fun findQueuesByLocation(lat: Double, lng: Double): List<QueueData> {
        // 0.5km radius for considering duplicates
        val radiusInMeters = 500
        return firebaseDataSource.findQueuesNearLocation(lat, lng, radiusInMeters)
    }

    override suspend fun updateAllWaitTimes() {
        val queues = firebaseDataSource.getAllQueues()
        queues.forEach { queue ->
            val updatedWaitTime = queue.currentWaitTime
            if (updatedWaitTime != queue.waitingTime) {
                firebaseDataSource.updateWaitTime(
                    queueId = queue.id,
                    newTime = updatedWaitTime
                )
            }
        }
    }

    override suspend fun removeQueues(queueIds: List<String>) {
        queueIds.chunked(500).forEach { batch -> // Firestore batch limit
            firebaseDataSource.removeQueues(batch)
        }
    }

    override suspend fun findExistingAtLocation(lat: Double, lng: Double): QueueData? {
        return firebaseDataSource.findNearbyQueues(lat, lng, 500)
            .firstOrNull()

    }

    override suspend fun updateWaitTime(queueId: String, newTime: Int) {

        Log.d("QueueRepositoryImpl", "Updating wait time for queue ID: $newTime")
        firebaseDataSource.queueCollection.document(queueId).update("waitingTime", newTime).await()
    }
}