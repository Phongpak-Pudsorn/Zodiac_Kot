package com.starvision

import android.app.Activity
import android.content.Context
import android.os.Build
import java.util.*

object AppPreferences {

    private const val APP_SHARED_PREFS = "AppPreferences"
    const val KEY_PREFS_JSON_DATA = "KEY_PREFS_JSON_DATA"
    const val KEY_PREFS_TIME_LOADS = "KEY_PREFS_TIME_LOADS"
    const val KEY_PREFS_UUID = "KEY_PREFS_UUID"
    const val KEY_PREFS_CCU_TIME_LOADS = "KEY_PREFS_CCU_TIME_LOADS"
    const val KEY_PREFS_OPEN_APP_FIRST = "open_app_first"
    const val KEY_PREFS_OPEN_APP_FIRST_CALENDAR = "open_app_first_calendar"
    const val KEY_PREFS_SEND_FRIEND_CONTACT = "send_mobile_contact"
    const val KEY_PREFS_SEND_MOBILE_DATA = "send_mobile_data"
    const val KEY_PREFS_CCU_URL = "KEY_PREFS_CCU_URL"

    fun setPreferences(mContext: Context,key: String, objects: Any){
        val preferences = mContext.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE)
        val prefer = preferences.edit()
        when (objects) {
            is String -> {
                val encoded: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    Base64.getEncoder().encodeToString(objects.toString().toByteArray())
                } else {
                    android.util.Base64.encodeToString(objects.toString().toByteArray(),0)
                }
                prefer.putString(key, encoded)
//                Log.e("encoded",""+encoded)
            }
            is Int -> {
                prefer.putInt(key, objects)
            }
            is Boolean -> {
                prefer.putBoolean(key, objects)
            }
            is Float -> {
                prefer.putFloat(key, objects as Float)
            }
            is Long -> {
                prefer.putLong(key, objects as Long)
            }
        }
        prefer.apply()
    }

    fun getPreferences(mContext: Context, key: String, objects: Any): Any? {
        val preferences = mContext.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE)
        when (objects) {
            is String -> {
                val pref = preferences.getString(key, objects)
                val decoded = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        String(Base64.getDecoder().decode(pref))
                    }
                    else {
                        android.util.Base64.decode(pref,0)
                    }
                    //.try ↓
                } catch (e: Exception) {
                    pref
                }
//                Log.e("decoded", "pref $pref")
//                Log.e("decoded",""+decoded)
                return decoded

            }
            is Int -> {
                return preferences.getInt(key, objects)
            }
            is Boolean -> {
                return preferences.getBoolean(key, objects)
            }
            is Float -> {
                return preferences.getFloat(key, objects)
            }
            is Long -> {
                return preferences.getLong(key, objects)
            }
            else -> return null
        }
    }
}