package com.smileapp.zodiac.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient {
    var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        retrofit = Retrofit.Builder()
            .baseUrl(Url.baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit!!
    }
}