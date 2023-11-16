package com.smileapp.zodiac.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object Utils {
    private val namePreferences = "zdaadmob"
    private val modePreferences = Context.MODE_PRIVATE
    private var sharedPrefs: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null
    const val KEY_STATUS_PUSNOTIFICATION = "Status"
    const val APP_SHARED_PREFS = "mAppPreferences"
    const val KEY_STATUSNOTIC = "KEY_STATUSNOTIC"
    var UUID = ""
    var currentFragment = 0

    fun AppPreference(context: Context) {
//    	Log.e("TAG", APP_SHARED_PREFS);
        sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE)
        prefsEditor = sharedPrefs!!.edit()

    }
    fun getStatusPushNoticfication(): Boolean? {
        return sharedPrefs!!.getBoolean(KEY_STATUS_PUSNOTIFICATION, false)
    }
    fun setStatusNotic(status:Boolean){
        prefsEditor!!.putBoolean(KEY_STATUSNOTIC,status)
        prefsEditor!!.commit()
    }
    fun getStatusNotic(): Boolean {
        return sharedPrefs!!.getBoolean(KEY_STATUSNOTIC,true)
    }

}