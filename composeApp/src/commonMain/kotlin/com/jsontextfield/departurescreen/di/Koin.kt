package com.jsontextfield.departurescreen.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.data.GoTrainDataSource
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.ui.AlertViewModel
import com.jsontextfield.departurescreen.ui.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.HEADERS
            }
            defaultRequest {
                url("https://api.openmetrolinx.com/OpenDataAPI/api/V1/")
            }
        }
    }
}

val dataModule = module {
    single<DepartureScreenAPI> { DepartureScreenAPI(get<HttpClient>()) }
    single<IGoTrainDataSource> {
//        FakeGoTrainDataSource()
        GoTrainDataSource(get<DepartureScreenAPI>())
    }
}

expect fun preferencesModule(): Module

val viewModelModule = module {
    factoryOf(::MainViewModel)
    factoryOf(::AlertViewModel)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            networkModule,
            dataModule,
            viewModelModule,
            preferencesModule(),
        )
    }
}

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

internal const val dataStoreFileName = "union_departures.preferences_pb"
