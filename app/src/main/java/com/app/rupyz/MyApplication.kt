package com.app.rupyz

import android.app.Application
import com.app.rupyz.databse.DatabaseLogManager
import com.google.android.libraries.places.api.Places
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions

class MyApplication : Application() {
    private var actionPerformed = false

    override fun onCreate() {
        super.onCreate()
        instance = this

        SentryAndroid.init(this) { options: SentryAndroidOptions ->
            // Add your DSN here
            options.dsn = "https://fc2d9f1d2827ad683a9da3ea02f8c2fd@o4506931751092224.ingest.us.sentry.io/4506935938383872"
        }

        DatabaseLogManager.getInstance().initializedDB(this)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        }
    }

    // Getter and Setter methods for the variable
    fun getActionPerformedValue(): Boolean {
        return actionPerformed
    }

    fun setPerformedValue(value: Boolean) {
        this.actionPerformed = value
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}