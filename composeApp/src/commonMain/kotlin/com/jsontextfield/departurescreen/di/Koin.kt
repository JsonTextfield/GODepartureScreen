package com.jsontextfield.departurescreen.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.data.TransitRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
import com.jsontextfield.departurescreen.core.domain.SetFavouriteStopUseCase
import com.jsontextfield.departurescreen.core.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.TripDetailsViewModel
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    singleOf(::DepartureScreenAPI)
}

val dataModule = module {
    single<ITransitRepository> {
        val useFake = false
        if (useFake) {
            FakeTransitRepository()
        } else {
            TransitRepository(get<DepartureScreenAPI>())
        }
    }
}

expect fun preferencesModule(): Module

expect fun widgetModule(): Module

val viewModelModule = module {
    factoryOf(::GetSelectedStopUseCase)
    factoryOf(::SetFavouriteStopUseCase)
    viewModelOf(::MainViewModel)
    viewModelOf(::AlertsViewModel)
    viewModelOf(::StopsViewModel)
    viewModel { params ->
        StopsViewModel(
            getSelectedStopUseCase = get<GetSelectedStopUseCase>(),
            setFavouriteStopUseCase = get<SetFavouriteStopUseCase>(),
            goTrainDataSource = get<ITransitRepository>(),
            preferencesRepository = get<IPreferencesRepository>(),
            selectedStopCode = params.getOrNull(String::class),
        )
    }
    viewModel { params ->
        TripDetailsViewModel(
            goTrainDataSource = get<ITransitRepository>(),
            selectedStop = params[0],
            tripId = params[1],
            lineCode = params[2],
            destination = params[3],
        )
    }
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
