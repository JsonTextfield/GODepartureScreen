package com.jsontextfield.departurescreen.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.core.data.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.GoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsViewModel
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    singleOf(::DepartureScreenAPI)
}

val dataModule = module {
    single<IGoTrainDataSource> {
        val useFake = false
        if (useFake) {
            FakeGoTrainDataSource()
        } else {
            GoTrainDataSource(get<DepartureScreenAPI>())
        }
    }
}

expect fun preferencesModule(): Module

expect fun widgetModule(): Module

val viewModelModule = module {
    factoryOf(::DepartureScreenUseCase)
    viewModelOf(::MainViewModel)
    viewModelOf(::AlertsViewModel)
    viewModelOf(::StationsViewModel)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            networkModule,
            dataModule,
            viewModelModule,
            preferencesModule(),
            widgetModule(),
        )
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

internal const val dataStoreFileName = "union_departures.preferences_pb"
