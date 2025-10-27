package com.jsontextfield.departurescreen.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.core.data.DataStorePreferencesRepository
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetViewModel
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun preferencesModule(): Module {
    return module {
        single<IPreferencesRepository> {
            val appGroupId = "group.com.jsontextfield.godepartures"
            DataStorePreferencesRepository(
                dataStore = createDataStore(),
                onSetStation = { stationCode: String ->
                    val selectedStationCodeKey = "selectedStationCode"
                    val userDefaults = NSUserDefaults(suiteName = appGroupId)
                    userDefaults.setObject(stationCode, forKey = selectedStationCodeKey)
                },
                onSetSortMode = { sortMode ->
                    val sortModeKey = "sortMode"
                    val userDefaults = NSUserDefaults(suiteName = appGroupId)
                    userDefaults.setInteger(sortMode.ordinal.toLong(), forKey = sortModeKey)
                },
                onSetVisibleTrains = { visibleTrains ->
                    val hiddenTrainsKey = "hiddenTrains"
                    val userDefaults = NSUserDefaults(suiteName = appGroupId)
                    userDefaults.setObject(visibleTrains.joinToString(","), forKey = hiddenTrainsKey)
                },
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        val appGroupId = "group.com.jsontextfield.godepartures"

        val sharedContainer: NSURL? =
            NSFileManager.defaultManager.containerURLForSecurityApplicationGroupIdentifier(appGroupId)

        val baseURL = sharedContainer ?: NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(baseURL).path + "/$dataStoreFileName"
    }
)


class WidgetHelper : KoinComponent {
    val preferencesRepository: IPreferencesRepository by inject()
    val goTrainDataSource: IGoTrainDataSource by inject()
    val departureScreenUseCase = DepartureScreenUseCase(
        goTrainDataSource = goTrainDataSource,
        preferencesRepository = preferencesRepository,
    )
    val widgetViewModel = WidgetViewModel(
        departureScreenUseCase = departureScreenUseCase,
        goTrainDataSource = goTrainDataSource,
        preferencesRepository = preferencesRepository,
    )
}