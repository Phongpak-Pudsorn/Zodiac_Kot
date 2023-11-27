package com.smileapp.zodiac.model

data class ZodiacServerInfo(val Status:String,
                            val StatusServer:ArrayList<ServerData>,
                            val Version:ArrayList<VersionData>){
    data class ServerData(val status_id:Int,
                          val message:String,
                          val active:Boolean,
                          val creationdate:String,
                          val modificationdate:String){
    }
    data class VersionData(val id:Int,
                           val message_alert_update_version:String,
                           val current_version:String,
                           val priority:String,
                           val link:String,
                           val creationdate:String,
                           val modificationdate:String,
                           val platform:String,
                           val banner_status:String,
                           val popup_status:String,
                           val push_active:Boolean,
                           val popup_size:String,
                           val modify_by:String,
                           val monthly:String,
                           val yearly:String,
                           val isontracking:Boolean){
    }
}
