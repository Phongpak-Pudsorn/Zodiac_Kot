package com.smileapp.zodiac.model

data class NewsInfo(val Status:String,
                    val Datarow:ArrayList<DataInfo>){
    data class DataInfo(val artide_id:String,
                        val title:String,
                        val image:String,
                        val total_view:String,
                        val artide_date:String,
                        val link_title:String,
                        val link_vdo:String,
                        val pin:Boolean){

    }
}
