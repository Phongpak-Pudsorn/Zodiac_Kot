package com.smileapp.zodiac

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance
import com.google.android.gms.ads.MobileAds

class MyApplication:Application() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null
        fun getContext(): Context {
            return mContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        MobileAds.initialize(this) {}

        FlurryAgent.Builder()
            .withDataSaleOptOut(false) //CCPA - the default value is false
            .withCaptureUncaughtExceptions(true)
            .withIncludeBackgroundSessionsInMetrics(true)
            .withLogLevel(Log.VERBOSE)
            .withPerformanceMetrics(FlurryPerformance.ALL)
            .build(this, "7KT3QQYF2ZN8RFJGGMBS")
    }
}