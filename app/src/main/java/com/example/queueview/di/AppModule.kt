package com.example.queueview.di

import android.content.Context
import com.example.queueview.data.remote.FirebaseDataSource
import com.example.queueview.data.remote.LocationClient
import com.example.queueview.data.repository.QueueRepository
import com.example.queueview.data.repository.QueueRepositoryImpl
import com.example.queueview.presentation.viemodel.FormViewModel
import com.example.queueview.presentation.viemodel.MainViewModel
import com.example.queueview.presentation.viemodel.SearchViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Core dependencies
    //single { androidContext().applicationContext }

    // 2. Firebase (initialize FIRST)
    single {
        FirebaseFirestore.getInstance().apply {
            // Enable Firestore logging for debugging
            FirebaseFirestore.setLoggingEnabled(true)
        }
    }

    // 3. Location Services (add timeout)
    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(get<Context>()).apply {
            // Verify Google Play Services availability
            if (GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(get()) != ConnectionResult.SUCCESS
            ) {
                throw IllegalStateException("Google Play Services unavailable")
            }
        }
    }

    // 4. Data sources (add null checks)
    single<FirebaseDataSource> {
        FirebaseDataSource().also {
            println("FirebaseDataSource initialized") // Debug log
        }
    }


    // Location Services
//    single<FusedLocationProviderClient> {
//        LocationServices.getFusedLocationProviderClient(get())
//    }

    // 6. Repository (lazy init)
    single<QueueRepository> {
        QueueRepositoryImpl(get()).also {
            println("Repository initialized") // Debug log
        }
    }

    // 5. Location client (with fallback)
    single<LocationClient> {
        LocationClient(
            context = get(),
            client = get()
        ).also {
            println("LocationClient initialized") // Debug log
        }
    }

    viewModel { MainViewModel(get()) }
    viewModel { FormViewModel(get(), get()) }
    viewModel { SearchViewModel() }

}