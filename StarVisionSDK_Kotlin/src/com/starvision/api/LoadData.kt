package com.starvision.api

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.starvision.AppPreferences
import com.starvision.Const
import com.starvision.models.AdsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class LoadData(val mContext : Context) {
    private val tagS = this.javaClass.simpleName
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("HH:mm:ss")
    var checkData : Boolean? = null

    fun loadAdsData(){
        Const.log(tagS, "loadAdsData")
        val packageName = mContext.packageName
        val getURL = ApiClient().getClientBaseURL().create(Api::class.java)
        getURL.getDataAds(packageName).enqueue(object : Callback<AdsModel> {
            override fun onResponse(call: Call<AdsModel>, response: Response<AdsModel>) {
                Const.log(tagS,"LoadData call : "+call.request().url())
                try {
                    val jResultIn = Gson().toJson(response.body()!!, AdsModel::class.java)
                    if( jResultIn != null ){
                        AppPreferences.setPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA, jResultIn)
                        AppPreferences.setPreferences(mContext, AppPreferences.KEY_PREFS_TIME_LOADS, dateFormat.format(System.currentTimeMillis()))
                        checkData = true
                        Const.log(tagS, "checkData :$checkData")

                    }else{
                        // load admob
                        checkData = false
                        Const.log(tagS, "checkData :$checkData")
                    }
                }catch (e : java.lang.Exception){
                    checkData = false
                    Const.log(tagS, "catch")
                    Const.log(tagS, "checkData :$checkData")
                }
            }
            override fun onFailure(call: Call<AdsModel>, t: Throwable) {
                checkData = false
                Const.log(tagS, "onFailure")
                Const.log(tagS, "checkData :$checkData")
            }
        })
    }
}