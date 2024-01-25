package com.starvision

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log


object Const {
    fun log(strClass :String,text :String){
//        Log.e(strClass,text)
    }
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(activity: Activity): Int {
        var result = 0
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = activity.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun openPlayStore(context: Context, destinationPackageName: String) {
        try {
            if (appInstalledOrNot(context, "com.android.vending")) {
                val sendIntent = Intent(Intent.ACTION_VIEW)
                sendIntent.data = Uri.parse("market://details?id=$destinationPackageName")
                sendIntent.setClassName(
                    "com.android.vending",
                    "com.google.android.finsky.activities.LaunchUrlHandlerActivity"
                )
                context.startActivity(sendIntent)
            } else {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$destinationPackageName")
                    )
                )
            }
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$destinationPackageName")
                )
            )
            anfe.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun appInstalledOrNot(mContext: Context, uri: String): Boolean {
//        log("Const","uri : $uri")
        val appInstalled: Boolean = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mContext.packageManager.getApplicationInfo(mContext.packageName,
                    PackageManager.ApplicationInfoFlags.of(0))
            } else {
                mContext.packageManager.getApplicationInfo(mContext.packageName, PackageManager.GET_META_DATA)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return appInstalled
    }

}