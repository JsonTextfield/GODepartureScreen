package com.jsontextfield.departurescreen.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.data.FakeGoTrainDataSource
import com.jsontextfield.departurescreen.data.GoTrainDataSource
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.network.API_KEY
import com.jsontextfield.departurescreen.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.ui.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
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
                level = LogLevel.INFO
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.openmetrolinx.com"
                    encodedPath = "/OpenDataAPI/api/V1/"
                    parameters.append("key", API_KEY)
                }
            }
        }
    }
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

val viewModelModule = module {
    factoryOf(::MainViewModel)
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
