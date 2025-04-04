package com.example.queueview.data.remote

import com.example.queueview.data.model.QueueData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()
    val queueCollection = db.collection("queues")

    //Adding new queue to firestore
    suspend fun addQueueData(queueData: QueueData): String {
        val documentReference = queueCollection.add(queueData.toMap()).await()
        return documentReference.id
    }

    //Fetch all queues(For testing only)
    suspend fun getAllQueues(): List<QueueData> {

        val snapshot = queueCollection.get().await()
        return snapshot.documents.map { doc ->
            doc.toObject(QueueData::class.java)?.copy(id = doc.id)
                ?: throw Exception("Invalid data")

        }
    }

    suspend fun removeQueues(queueIds: List<String>) {
        val batch = db.batch()
        queueIds.forEach { id ->
            batch.delete(queueCollection.document(id))
        }
        batch.commit().await()
    }

    suspend fun updateWaitTime(queueId: String, newTime: Int) {
        queueCollection.document(queueId).update(
            mapOf(
                "waitingTime" to newTime,
                "lastUpdated" to Timestamp.now(),
            )
        ).await()
    }

    suspend fun findQueuesNearLocation(
        lat: Double,
        lng: Double,
        radiusInMeters: Int
    ): List<QueueData> {
        // Reference point
        val center = GeoPoint(lat, lng)

        // Each degree of latitude ~111km
        val latDelta = radiusInMeters / 111320.0
        val bounds = listOf(
            GeoPoint(lat - latDelta, lng),
            GeoPoint(lat + latDelta, lng)
        )

        return queueCollection
            .whereGreaterThan("latitude", bounds[0].latitude)
            .whereLessThan("latitude", bounds[1].latitude)
            .get()
            .await()
            .toObjects(QueueData::class.java)
            .filter { queue ->
                val distance = calculateDistance(
                    center.latitude,
                    center.longitude,
                    queue.latitude,
                    queue.longitude
                )
                distance <= radiusInMeters
            }
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        // Simplified Haversine formula
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        return 6371000 * 2 * atan2(sqrt(a), sqrt(1 - a)) // Earth radius in meters
    }

    suspend fun findNearbyQueues(lat: Double, lng: Double, radiusMeters: Int): List<QueueData> {
        val latDelta = radiusMeters / 111320.0
        return queueCollection
            .whereGreaterThan("latitude", lat - latDelta)
            .whereLessThan("latitude", lat + latDelta)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.toObject(QueueData::class.java)?.copy(id = doc.id) // Include document ID
            }
            .filter { queue ->
                // Simple distance check (improve later)
                abs(queue.longitude - lng) < latDelta
            }
    }
}
