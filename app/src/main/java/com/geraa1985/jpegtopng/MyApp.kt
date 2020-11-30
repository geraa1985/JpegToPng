package com.geraa1985.jpegtopng

import android.app.Application
import io.reactivex.rxjava3.plugins.RxJavaPlugins

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler {  }
    }
}