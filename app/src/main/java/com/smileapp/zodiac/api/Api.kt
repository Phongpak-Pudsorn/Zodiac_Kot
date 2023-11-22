package com.smileapp.zodiac.api

import com.smileapp.zodiac.model.NewsInfo
import com.smileapp.zodiac.model.ZodiacTodayInfo
import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("/mobileweb/appsmartdirect/zodiac/serverweb/services/datajson/file_horoscope_daily.json")
    fun getToday(): Call<ZodiacTodayInfo>
}