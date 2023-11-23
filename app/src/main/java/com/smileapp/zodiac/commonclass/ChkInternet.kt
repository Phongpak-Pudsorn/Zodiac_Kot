package com.smileapp.zodiac.commonclass

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
class ChkInternet(context: Context) {
    var statusConnectionInternet = false
    var chkConnection: Activity

    init {
        chkConnection = context as Activity
    }

    fun chkConnectionStatus() {
        val connMgr =
            chkConnection.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        statusConnectionInternet = if (wifi!!.isAvailable) {
            //Toast.makeText(this, "Wifi" , Toast.LENGTH_LONG).show();
            true
        } else mobile!!.isAvailable
    }

    //	public String getStaticIPAddress(){
    val isOnline: Boolean
        get() {
            val cm =
                chkConnection.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

}