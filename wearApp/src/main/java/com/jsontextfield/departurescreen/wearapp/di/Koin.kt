package com.jsontextfield.departurescreen.wearapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.core.data.DataStorePreferencesRepository
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.data.TransitRepository
import com.jsontextfield.departurescreen.core.data.fake.FakeTransitRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
import com.jsontextfield.departurescreen.core.domain.SetFavouriteStopUseCase
import com.jsontextfield.departurescreen.core.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
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

val preferencesModule = module {
    single<IPreferencesRepository> {
        DataStorePreferencesRepository(
            createDataStore(
                producePath = {
                    androidContext().filesDir.resolve(dataStoreFileName).absolutePath
                }
            )
        )
    }
}

val viewModelModule = module {
    factoryOf(::GetSelectedStopUseCase)
    factoryOf(::SetFavouriteStopUseCase)
    factoryOf(::MainViewModel)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            networkModule,
            dataModule,
            viewModelModule,
            preferencesModule,
        )
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

internal const val dataStoreFileName = "union_departures.preferences_pb"
