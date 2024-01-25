package com.starvision.ccusdk

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.starvision.*
import com.starvision.api.Api
import com.starvision.api.ApiClient
import com.starvision.api.URL
import com.starvision.models.CcuModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class StarVisionCcuSDK(context : Context) {
    private val tagS = this.javaClass.simpleName
    private var mContext = context
    private var appPrefs = AppPreferences
    private var timeLoadCcu : Long? = null
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

    fun startService(Port: String,UUID: String,PackageName: String,rootName: String) {
        Const.log(tagS,"startService : ")
        try {
            URL.BASE_URL = "http://www.starvision.in.th:$Port"
            Const.log(tagS,"URL.BASE_URL : "+URL.BASE_URL)
            val getURL = ApiClient().getClientBaseURL().create(Api::class.java)
            getURL.getDataCCU(UUID,PackageName,rootName).enqueue(object : Callback<CcuModel> {
                override fun onResponse(call: Call<CcuModel>, response: Response<CcuModel>) {
                    try {
                        Const.log(tagS,"call : "+call.request().url())
                        URL.BASE_URL = "https://www.starvision.in.th"
                        Const.log(tagS,"URL.BASE_URL : "+URL.BASE_URL)
                        val cCU = Gson().toJson(response.body()!!, CcuModel::class.java)
                        timeLoadCcu = System.currentTimeMillis()
                        appPrefs.setPreferences(mContext,appPrefs.KEY_PREFS_UUID,UUID)
                        appPrefs.setPreferences(mContext,appPrefs.KEY_PREFS_CCU_URL,cCU)
                        appPrefs.setPreferences(mContext,appPrefs.KEY_PREFS_CCU_TIME_LOADS, dateFormat.format(timeLoadCcu))
                        Const.log(tagS, "CCU : $cCU")
                    }catch (e :Exception){
                        URL.BASE_URL = "https://www.starvision.in.th"
                        Const.log(tagS,"onResponse catch Cannot Accept Link")
                    }
                }
                override fun onFailure(call: Call<CcuModel>, t: Throwable) {
                    URL.BASE_URL = "https://www.starvision.in.th"
                    Const.log(tagS,"call : "+call.request().url())
                    Const.log(tagS, "onFailure : Throwable : $t")
                }
            })
        } catch (e : Exception) {
            URL.BASE_URL = "https://www.starvision.in.th"
            Const.log(tagS,"catch : CCU Error")
            e.printStackTrace()
        }
    }
}