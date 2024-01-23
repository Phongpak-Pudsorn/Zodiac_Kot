package com.smileapp.zodiac.commonclass

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
class ChkInternet(mContext: Context) {
    var statusConnectionInternet = false
    var chkConnection: Activity

    init {
        chkConnection = mContext as Activity
    }

//    fun chkConnectionStatus() {
//        val connMgr =
//            chkConnection.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//        val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//        statusConnectionInternet = if (wifi!!.isAvailable) {
//            //Toast.makeText(this, "Wifi" , Toast.LENGTH_LONG).show();
//            true
//        } else mobile!!.isAvailable
//    }

    val isOnline: Boolean
        get() {
            val result: Boolean
            val connectivityManager =chkConnection.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
            return result
        }

}