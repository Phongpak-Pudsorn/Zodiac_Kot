package com.starvision.installsdk

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.view.ContextThemeWrapper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starvision.*
import com.starvision.ConstVisionInstallSDK.bSendContact
import com.starvision.ConstVisionInstallSDK.strPacketNameInstall
import com.starvision.api.Api
import com.starvision.api.ApiClient
import com.starvision.bannersdk.R
import com.starvision.models.CcuModel
import com.starvision.models.ContactModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class StarVisionInstallSDK(context : Context) {
    private val mContext = context
    private val appPrefs = AppPreferences
    private var mCalendarNow = Calendar.getInstance()
    private val mCalendarFirst = Calendar.getInstance()
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("D")
    private val tagS = javaClass.simpleName

    fun setUrlInstall(strUrlInstall: String) {
        ConstVisionInstallSDK.strUrlInstall = strUrlInstall
        ConstVisionInstallSDK.bSetUrlInstall = true
    }

    fun setPacketNameInstall(strPacketNameInstall: String) {
        ConstVisionInstallSDK.strPacketNameInstall = strPacketNameInstall
        ConstVisionInstallSDK.bSetPacketName = true
    }

    fun setAppBannerID(strAppBannerID: String) {
        ConstVisionInstallSDK.strAppBannerID = strAppBannerID
        ConstVisionInstallSDK.bSetAppBannerID = true
    }

    fun setSendContact(bSendContact: Boolean) {
        ConstVisionInstallSDK.bSendContact = bSendContact
    }

    fun setGetAccount(bGetAccount: Boolean) {
        ConstVisionInstallSDK.bGetAccount = bGetAccount
    }

    private fun runService(){
        if(strPacketNameInstall == ""){
            ConstVisionInstallSDK.bSetPacketName = false
        }
        if(ConstVisionInstallSDK.strUrlInstall == ""){
            ConstVisionInstallSDK.bSetUrlInstall = false
        }
        if(ConstVisionInstallSDK.strAppBannerID == ""){
            ConstVisionInstallSDK.bSetAppBannerID = false
        }
        if((!ConstVisionInstallSDK.bSetPacketName) || (!ConstVisionInstallSDK.bSetUrlInstall) || (!ConstVisionInstallSDK.bSetAppBannerID)){
			Const.log(tagS, "YOU CAN NOT SET PARAMS, PLEASE CHECK")
            return
        }
        Const.log(tagS,"KEY_PREFS_OPEN_APP_FIRST "+appPrefs.getPreferences(mContext,appPrefs.KEY_PREFS_OPEN_APP_FIRST,Boolean).toString())
        if(appPrefs.getPreferences(mContext,appPrefs.KEY_PREFS_OPEN_APP_FIRST,false) == false){
            if(isOnline(mContext)){
                Const.log(tagS, "isOnline")
                if(appPrefs.getPreferences(mContext,appPrefs.KEY_PREFS_SEND_MOBILE_DATA,false) == false){
                    Const.log(tagS, "KEY_PREFS_SEND_MOBILE_DATA : false")
                    callGetContact = CallGetContact(strPacketNameInstall, false)
                    callGetContact.execute()
                } else {
                    if(bSendContact){
                        Const.log(tagS, "bSendContact : True")
                        callGetContact = CallGetContact(strPacketNameInstall, true)
                        callGetContact.execute()
                    }
                }
            }
        } else {
            mCalendarNow.timeInMillis = dateFormat.format(mCalendarNow.timeInMillis).toLong()
            mCalendarFirst.timeInMillis = appPrefs.getPreferences(mContext,appPrefs.KEY_PREFS_OPEN_APP_FIRST_CALENDAR,mCalendarFirst.timeInMillis) as Long
            Const.log(tagS,"appPre mCalendarFirst : "+ appPrefs.getPreferences(mContext,appPrefs.KEY_PREFS_OPEN_APP_FIRST_CALENDAR,mCalendarFirst.timeInMillis) as Long)
            Const.log(tagS,"dateFormat mCalendarFirst : "+ dateFormat.format(mCalendarFirst.timeInMillis))
            Const.log(tagS,"dateFormat mCalendarNow : "+ mCalendarNow.timeInMillis)
            if(daysBetween(mCalendarFirst, mCalendarNow) > 365){
                Const.log(tagS, "RESET OPEN APP FIRST")
                appPrefs.setPreferences(mContext,appPrefs.KEY_PREFS_OPEN_APP_FIRST,false)
                appPrefs.setPreferences(mContext,appPrefs.KEY_PREFS_SEND_MOBILE_DATA,false)
                appPrefs.setPreferences(mContext,appPrefs.KEY_PREFS_SEND_FRIEND_CONTACT,false)
                if(isOnline(mContext)){
                    Const.log(tagS, "isOnline")
                    if(appPrefs.getPreferences(mContext,appPrefs.KEY_PREFS_SEND_FRIEND_CONTACT,Boolean) == false){
                        Const.log(tagS, "KEY_PREFS_SEND_FRIEND_CONTACT : False")
                        callGetContact = CallGetContact(strPacketNameInstall, false)
                        callGetContact.execute()
                    } else {
                        if(bSendContact){
                            Const.log(tagS, "KEY_PREFS_SEND_FRIEND_CONTACT : True")
                            callGetContact = CallGetContact(strPacketNameInstall, true)
                            callGetContact.execute()
                        }
                    }
                }
            }
        }
    }

    fun startService() {
        if (bSendContact) {
            val dialog = AlertDialog.Builder(ContextThemeWrapper(mContext, R.style.AppTheme))
            dialog.setMessage(R.string.grant_message)
            dialog.setPositiveButton(R.string.grant
            ) { Dialog, _ ->
                runService()
                Dialog.cancel()
            }
            dialog.setNegativeButton(R.string.no_grant
            ) { Dialog, _ ->
                bSendContact = false
                runService()
                Dialog.cancel()
            }
            dialog.create()
            dialog.show()
        } else {
            runService()
        }
    }

    private fun isOnline(context: Context): Boolean {
        var result = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
        return result
    }

    private fun daysBetween(startDate: Calendar, endDate: Calendar?): Long {
        val date = startDate.clone() as Calendar
        var daysBetween: Long = 0
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1)
            daysBetween++
        }
        Const.log(tagS, "DATE BETWEEN: $daysBetween")
        return daysBetween
    }


    lateinit var callGetContact : CallGetContact
    @SuppressLint("StaticFieldLeak")
    inner class CallGetContact(private val strPacket : String, private val bGetFriendContact : Boolean) : ViewModel()  {
        private var bRunning = true
        private var strEmi = ConstVisionInstallSDK.getUUID(mContext)!!
        private var arrayContact = ContactModel()
        private val dataMachine = ContactModel.DataDetailRow()
        fun execute() = viewModelScope.launch {
            val result = doInBackground() // runs in background thread without blocking the Main Thread
            onPostExecute(result)
        }

        @SuppressLint("HardwareIds")
        private suspend fun doInBackground() : String = withContext(Dispatchers.IO) {
            while (bRunning){
                if (execute().isCancelled){
                    break
                } else {
                    try {
                        dataMachine.Brand = Build.BRAND
                        dataMachine.OS = "Android"
                        dataMachine.Device_Name = Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
                        dataMachine.Model = Build.MODEL
                        dataMachine.IMEI_UDID = strEmi
                        dataMachine.System_Version = ConstVisionInstallSDK.getAPIVersion()
                        dataMachine.Platform = ConstVisionInstallSDK.getAPIVersion()

                        arrayContact = ContactModel(strEmi,strPacket,ConstVisionInstallSDK.strAppBannerID,"Thailand"
                        ,"R71D1A6E23A8DB372E9E9D33E3CB4AB38D0A5",dataMachine)

                        Const.log(tagS, "CallGetContact : ")
                        Const.log(tagS, "ContactModel : "+arrayContact.machineIMEI
                                +" "+arrayContact.appAdsPackage
                                +" "+arrayContact.appBannerId
                                +" "+arrayContact.appBannerCountry
                                +" "+arrayContact.checked)
                        Const.log(tagS, "dataMachine : "+dataMachine.OS
                                +" "+dataMachine.Brand
                                +" "+dataMachine.Model
                                +" "+dataMachine.Device_Name
                                +" "+dataMachine.IMEI_UDID
                                +" "+dataMachine.Platform
                                +" "+dataMachine.System_Version)
                        bRunning = false
                    }catch (e : Exception){
                        Const.log(tagS, "catch :")
                        bRunning = false
                    }
                }
            }
            return@withContext ""
        }
        private fun onPostExecute(result: String){
            try {
                Const.log(tagS,"result : $result")
                if(!bGetFriendContact){
                    callWebServerInstall = CallWebServerInstall(ConstVisionInstallSDK.getGoogleUrl(strEmi), false)
                    callWebServerInstall.execute()
                } else {
                    callWebServerInstall = CallWebServerInstall(ConstVisionInstallSDK.getGoogleUrl(strEmi), false)
                    callWebServerInstall.execute()
                }
            }catch (e : Exception){
                Const.log(tagS, "catch :")
                e.printStackTrace()
            }
        }

    }

    lateinit var callWebServerInstall : CallWebServerInstall

    @SuppressLint("StaticFieldLeak")
    inner class CallWebServerInstall(private val googleUrl: String,private val b: Boolean) : ViewModel(){
        private var bRunning = true

        fun execute() = viewModelScope.launch {
            doInBackground() // runs in background thread without blocking the Main Thread
        }
        private suspend fun doInBackground() : String = withContext(Dispatchers.IO){
            Const.log(tagS,"googleUrl : $googleUrl")
            Const.log(tagS,"googleUrl : $b")
            try {
                while (bRunning) {
                    if (execute().isCancelled){
                        break
                    }else {
                        Const.log(tagS,"strUrlInstall : "+ConstVisionInstallSDK.strUrlInstall)
                        Const.log(tagS,"CallWebServerInstall")
                        com.starvision.api.URL.BASE_URL = "https://www.starvision.in.th"
                        val getURL = ApiClient().getClientBaseURL().create(Api::class.java)
                        getURL.getDataSDK().enqueue(object : Callback<CcuModel> {
                            override fun onResponse(call: Call<CcuModel>, response: Response<CcuModel>) {
                                Const.log(tagS,"call "+call.request().url() )
                                try {
                                    Const.log(tagS,"Status "+response.body()!!.Status )
                                    if (response.body()!!.Status == "True") {
                                        if (!bSendContact) {
                                            appPrefs.setPreferences(mContext, appPrefs.KEY_PREFS_SEND_MOBILE_DATA, true)
                                            if (bSendContact) {
                                                callGetContact = CallGetContact(strPacketNameInstall, true)
                                                callGetContact.execute()
                                            }
                                        } else {
                                            appPrefs.setPreferences(mContext, appPrefs.KEY_PREFS_SEND_FRIEND_CONTACT, true)
                                        }
                                        if (appPrefs.getPreferences(mContext, appPrefs.KEY_PREFS_SEND_FRIEND_CONTACT,true) as Boolean && appPrefs.getPreferences(mContext,appPrefs.KEY_PREFS_SEND_MOBILE_DATA,true) as Boolean) {
                                            appPrefs.setPreferences(mContext, appPrefs.KEY_PREFS_OPEN_APP_FIRST, true)
                                            appPrefs.setPreferences(mContext, appPrefs.KEY_PREFS_OPEN_APP_FIRST_CALENDAR, dateFormat.format(mCalendarNow.timeInMillis))
                                        }
                                    }
                                } catch (e: Exception) {
                                    Const.log(tagS, "catch :")
                                    e.printStackTrace()
                                }
                            }

                            override fun onFailure(call: Call<CcuModel>, t: Throwable) {
                                Const.log(tagS, "Call : $call")
                                Const.log(tagS, "onFailure : $t")
                            }
                        })
                        bRunning = false
                    }
                }
            }catch (e : Exception){
                Const.log(tagS, "catch :")
                e.printStackTrace()
                bRunning = false
            }
            return@withContext ""
        }
    }

}