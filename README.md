# QueueView - Built using Kotlin and Jetpack Compose

## Overview

QueueView is an app which helps the user to manage his time and avoid waiting in long queues. It runs on the crowdsourced data which is stored in Firebase Firesotre. QueueView uses OSM(Open Street Map) and Location-Play-Services to get the current location of the user. Queuview also allows the user to add the waiting of te place where he is in for the hepl of other people.
App uses realtime data in order to give the user the best experience. 
## Core Features:
 * A splash screen
 * The main screen with a lazy column and Contribute FAB from which the user can add details of the ongoing queue
 * The add queue data screen with a map inbuilt in order to show the user his current location
 * The app uses robust error handling to give the user best possible experience.

   
## Core Technologies
  * Jetpack Compose - Modern declarative UI toolkit
  * Firebase Firestore - NoSQL cloud database for queues data
      * Collections: queues, users
      * Real-time updates
* OpenStreetMap (osmdroid) - For map integration in the form
* Koin - Dependency injection
* Coroutines & Flow - For async operations and data streams

## Key Libraries

* Navigation-Compose - Screen navigation
* ViewModel - UI-related data holder
* LiveData/StateFlow - Observable data holders

# Architecture Components
* MVVM (Model-View-ViewModel) - Clean separation of concerns
* Repository Pattern - Abstraction layer between data sources and ViewModel
* Clean Architecture (Layered):
* Data Layer: FirebaseDataSource, QueueRepositoryImpl
* Domain Layer: QueueRepository interface, QueueData model
* UI Layer: Composables, MainViewModel

  # Fututre Goals:
  * Adding Search and Suggestion using the Nominatim API
  * Adding sort feature
  * WorkManager for auto refresh
    

  
