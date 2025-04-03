package com.example.queueview.data.remote

import com.example.queueview.data.model.QueueData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val queueCollection = db.collection("queues")

    //Adding new queue to firestore
    suspend fun addQueueData(queueData: QueueData): String {
        val documentReference = queueCollection.add(queueData.toMap()).await()
        return documentReference.id
    }

    //Fetch all queues(For testing only)
    suspend fun getAllQueues(): List<QueueData> {

        val snapshot = queueCollection.get().await()
        return snapshot.documents.map {doc ->
            doc.toObject(QueueData::class.java)?.copy(id = doc.id)?:throw Exception("Invalid data")

        }
    }
}
