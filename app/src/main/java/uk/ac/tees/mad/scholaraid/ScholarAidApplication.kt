package uk.ac.tees.mad.scholaraid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScholarAidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}