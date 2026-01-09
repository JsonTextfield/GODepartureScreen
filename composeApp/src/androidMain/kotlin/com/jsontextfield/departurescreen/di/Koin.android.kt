package com.jsontextfield.departurescreen.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jsontextfield.departurescreen.core.data.DataStorePreferencesRepository
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.widget.config.WidgetConfigDataStore
import com.jsontextfield.departurescreen.widget.config.WidgetConfigViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun preferencesModule(): Module {
    return module {
        single<DataStore<Preferences>> {
            createDataStore(androidContext())
        }
        single<IPreferencesRepository> {
            DataStorePreferencesRepository(get<DataStore<Preferences>>())
        }
    }
}

actual fun widgetModule(): Module {
    return module {
        single<WidgetConfigDataStore> {
            WidgetConfigDataStore(androidContext())
        }
        viewModel<WidgetConfigViewModel> { params ->
            WidgetConfigViewModel(
                goTrainDataSource = get<IGoTrainDataSource>(),
                preferencesRepository = get<IPreferencesRepository>(),
                widgetConfigDataStore = get<WidgetConfigDataStore>(),
                widgetId = params.getOrNull(Int::class),
            )
        }
    }
}

fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)