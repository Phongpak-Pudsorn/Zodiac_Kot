package com.starvision.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient {

    var retrofitAds : Retrofit? = null
    fun getClientBaseURL() :Retrofit{ retrofitAds = Retrofit.Builder()
        .baseUrl(URL.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        return retrofitAds!!}
}

