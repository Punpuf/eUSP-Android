package com.punpuf.e_bandejao

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import com.punpuf.e_bandejao.util.Utils
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

@HiltAndroidApp
class Application: Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        // logs
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())

        // ui mode
        Utils.updateUiMode(PreferenceManager.getDefaultSharedPreferences(applicationContext), applicationContext)
    }

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}