package com.smileapp.zodiac.model

data class ZodiacTodayInfo(val Status:String,
                           val Datarow:DataInfo){
    data class DataInfo(val daily_date:String,
                        val zodiactoday:ArrayList<TodayInfo>,
                        val credit:String){
        data class TodayInfo(val day:String,
                             val image:String,
                             val work:String,
                             val money:String,
                             val love:String)
    }

}
