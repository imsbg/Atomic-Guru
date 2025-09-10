package com.sandeep.atomicguru

import android.app.Application

class AtomicGuruApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set up the custom crash handler.
        // This will now catch any unhandled exceptions in your app.
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // You can also log to a remote server here if you want
            CrashHandler(applicationContext).uncaughtException(thread, throwable)
        }
    }
}