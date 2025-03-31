package com.droidslife.screensaver.di

import com.droidslife.screensaver.clock.ClockViewModel
import com.droidslife.screensaver.location.LocationService
import com.droidslife.screensaver.network.KtorClient
import com.droidslife.screensaver.settings.PreferencesRepository
import com.droidslife.screensaver.settings.PreferencesRepositoryImpl
import com.droidslife.screensaver.settings.SettingsViewModel
import com.droidslife.screensaver.weather.WeatherApi
import com.droidslife.screensaver.weather.WeatherRepository
import com.droidslife.screensaver.weather.WeatherViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for the application.
 */
val appModule = module {
    // HTTP Client
    single<HttpClient> { KtorClient.createWithRetry() }

    // Weather API
    single { WeatherApi(get()) }

    // Location Service
    single { LocationService() }

    // Preferences Repository
    single<PreferencesRepository> { PreferencesRepositoryImpl() }

    // Weather Repository
    single { WeatherRepository(get(), get()) }

    // Weather ViewModel
    factory { WeatherViewModel(get(), get(), get()) }

    // Clock ViewModel
    single { ClockViewModel() }

    // Settings ViewModel
    single { SettingsViewModel(get()) }
}
