package com.example.queueview.di

import com.example.queueview.data.remote.FirebaseDataSource
import com.example.queueview.data.repository.QueueRepository
import com.example.queueview.data.repository.QueueRepositoryImpl
import com.example.queueview.presentation.viemodel.FormViewModel
import com.example.queueview.presentation.viemodel.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        FirebaseDataSource()
    }

    single<QueueRepository> {
        QueueRepositoryImpl(get())// Injecting FirebaseDataSource
    }
    

    viewModel { MainViewModel(get()) }
    viewModel { FormViewModel(get()) }
}