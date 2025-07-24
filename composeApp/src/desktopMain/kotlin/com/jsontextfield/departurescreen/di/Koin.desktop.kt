package com.jsontextfield.departurescreen.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.jsontextfield.departurescreen.data.DataStorePreferencesRepository
import com.jsontextfield.departurescreen.data.IPreferencesRepository
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual fun preferencesModule(): Module {
    return module {
        single<IPreferencesRepository> {
            val file = File(System.getProperty("java.io.tmpdir"), dataStoreFileName)
            val dataStore = PreferenceDataStoreFactory.createWithPath(
                produceFile = { file.absolutePath.toPath() }
            )
            DataStorePreferencesRepository(dataStore)
        }
    }
}

