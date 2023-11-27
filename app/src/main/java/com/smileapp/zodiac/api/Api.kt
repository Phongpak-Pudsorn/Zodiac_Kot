package com.smileapp.zodiac.api

import com.smileapp.zodiac.model.NewsInfo
import com.smileapp.zodiac.model.ZodiacServerInfo
import com.smileapp.zodiac.model.ZodiacTodayInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("/mobileweb/appsmartdirect/zodiac/serverweb/services/datajson/file_horoscope_daily.json")
    fun getToday(): Call<ZodiacTodayInfo>
    @GET("/mobileweb/appsmartdirect/zodiac/serverweb/services/datajson/status_and_version_Android.json")
    fun getServer():Call<ZodiacServerInfo>
    @GET("{page}")
    fun getNews(@Path("page")page:String):Call<String>
    @GET("/mobileweb/appsmartdirect/zodiac/serverweb/services/data_return_json.php?OS=Android&request=artide&imeinumber=678def8c-a031-3428-9440-a5d593a3ce0e&pullup=false")
    fun getPullup():Call<NewsInfo>
    @GET("/mobileweb/appsmartdirect/zodiac/serverweb/services/data_return_json.php?OS=Android&request=artide&imeinumber=678def8c-a031-3428-9440-a5d593a3ce0e&pullup=true")
    fun getPulldown():Call<NewsInfo>
}