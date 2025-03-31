package com.droidslife.screensaver.di

import org.koin.core.context.startKoin
import org.koin.core.context.GlobalContext
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

/**
 * Initialize Koin for dependency injection.
 * This function should be called at the start of the application.
 * If Koin is already initialized, this function does nothing.
 */
fun initKoin(config : KoinAppDeclaration? = null){

    val koin = KoinPlatformTools.defaultContext().getOrNull()
    if (koin != null) return

    startKoin {
        printLogger()
        includes(config)
        modules(commonModule())
    }
}


fun initKoin() = initKoin {}

fun commonModule() = module {
}
