package com.jsontextfield.departurescreen.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.core.data.DataStorePreferencesRepository
import com.jsontextfield.departurescreen.core.data.FAVOURITE_STOPS_KEY
import com.jsontextfield.departurescreen.core.data.HIDDEN_TRAINS_KEY
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.data.SELECTED_STOP_CODE_KEY
import com.jsontextfield.departurescreen.core.data.SORT_MODE_KEY
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
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
                onSetStop = { stopCode: String ->
                    val userDefaults = NSUserDefaults(suiteName = appGroupId)
                    userDefaults.setObject(stopCode, forKey = SELECTED_STOP_CODE_KEY)
                },
                onSetSortMode = { sortMode ->
                    val userDefaults = NSUserDefaults(suiteName = appGroupId)
                    userDefaults.setInteger(sortMode.ordinal.toLong(), forKey = SORT_MODE_KEY)
                },
                onSetVisibleTrains = { visibleTrains ->
                    val userDefaults = NSUserDefaults(suiteName = appGroupId)
                    userDefaults.setObject(visibleTrains.joinToString(","), forKey = HIDDEN_TRAINS_KEY)
                },
                onSetFavouriteStops = { favouriteStops ->
                    val userDefaults = NSUserDefaults(suiteName = appGroupId)
                    userDefaults.setObject(favouriteStops.joinToString(","), forKey = FAVOURITE_STOPS_KEY)
                }
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
    val goTrainDataSource: ITransitRepository by inject()
    val getSelectedStopUseCase = GetSelectedStopUseCase(
        goTrainDataSource = goTrainDataSource,
        preferencesRepository = preferencesRepository,
    )
}

actual fun widgetModule(): Module {
    return module {  }
}