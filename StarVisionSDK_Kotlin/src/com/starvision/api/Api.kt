package com.starvision.api


import com.starvision.models.AdsModel
import com.starvision.models.CcuModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("/appbannersdk/serverweb/data_json/{packageName}_Android_TH.json")
    fun getDataAds(@Path("packageName") packageName : String): Call<AdsModel>

    @GET("/add_ccu_all_json/{UUID}/{packageName}/Android/{rootName}")
    fun getDataCCU(@Path("UUID") UUID : String,
                   @Path("packageName") packageName : String,
                   @Path("rootName") rootName : String): Call<CcuModel>

    @GET("/appbannersdk/serverweb/sdk_app_install.php")
    fun getDataSDK(): Call<CcuModel>

}