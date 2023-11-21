package com.smileapp.zodiac.api

import com.smileapp.zodiac.model.NewsInfo
import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET()
    fun getData(): Call<NewsInfo>
}