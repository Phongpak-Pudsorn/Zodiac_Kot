package com.smileapp.zodiac.pushnotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import android.R.string.no
import android.icu.number.IntegerWidth
import com.smileapp.zodiac.R
import com.smileapp.zodiac.api.Api
import com.smileapp.zodiac.api.ApiClient
import com.smileapp.zodiac.api.Url
import com.smileapp.zodiac.model.ServerInfo
import com.smileapp.zodiac.utils.Utils
import com.smileapp.zodiac.view.activity.MainActivity
import java.util.*
import kotlin.collections.ArrayList


class MyFirebaseMessagingService:FirebaseMessagingService() {
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
//        FileHelper.saveToFile("onNewToken: $token")
        Utils.Token = token
        CallSendingToken(token)
    }

    fun CallSendingToken(token:String){
        var bRunning = true
        var strIMEI = Utils.UUID
        executor.execute {
            try {
//                FileHelper.saveToFile("CallSendingToken doInBackground: try")
                while (bRunning){
                    try {
//                        FileHelper.saveToFile("CallSendingToken doInBackground: try1")
//                        Log.e("push Regis",Url.starBase+genRegisUrl(strIMEI,token))
                        val service = ApiClient().getClient().create(Api::class.java)
                        service.getlink(Url.starPath, Url.uiType,strIMEI, token, Url.starChecked).enqueue(object : Callback<String>{
                                override fun onResponse(
                                    call: Call<String>,
                                    response: Response<String>,
                                ) {
//                                    Log.e("Test url", response.raw().request().url().toString())
                                    if (response.isSuccessful) {
                                        Utils.setServerData(response.body().toString())
//                                        Log.e("Test", response.body().toString())
                                        bRunning = false
                                    }else{
//                                        Log.e("Test", "failed")
                                        bRunning = false
                                    }
                                }

                                override fun onFailure(call: Call<String>, t: Throwable) {
//                                    Log.e("Test",t.message.toString())
                                    bRunning = false
                                }
                            })

                    }catch (e:Exception){
//                        FileHelper.saveToFile("CallSendingToken doInBackground: catch1")
                        bRunning = false
                    }
                    break
                }

            }catch (e:Exception){
//                FileHelper.saveToFile("CallSendingToken doInBackground: catch")
                e.printStackTrace()
            }
            handler.post {
                try {
                    val data = Gson().fromJson(Utils.getServerData(), ServerInfo::class.java)
//                    Log.e("status",data.Status)
                    if (data!=null) {
                        if (data.Status=="True") {
//                            FileHelper.saveToFile("CallSendingToken Status: True")
                        } else {
//                            FileHelper.saveToFile("CallSendingToken Status: False")
                        }
                    }else{
//                        FileHelper.saveToFile("CallSendingToken no data from server")
                    }

                }catch (e:Exception){
                    e.printStackTrace()
//                    FileHelper.saveToFile("CallSendingToken onPostExecute: $e")
                }

            }
        }

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
//        		Log.e("remoteMessage","ID:"+remoteMessage.getMessageId());
//		Log.e("remoteMessage","Type:"+remoteMessage.getMessageType());
//		Log.e("remoteMessage","Data:"+remoteMessage.getData());
//		Log.e("remoteMessage","Notification:"+remoteMessage.getNotification());
//		Log.e("remoteMessage","Key:"+remoteMessage.getCollapseKey());
//		Log.e("remoteMessage","Priority:"+remoteMessage.getPriority());
//		Log.e("remoteMessage","Sent time:"+remoteMessage.getSentTime());


        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Utils.AppPreference(this)
//        FileHelper.saveToFile("onMessageReceived: " + message.messageId)
        val notification = message.notification
        val data = message.data



//        FileHelper.saveToFile("onMessageReceived StatusNotic before:" + Consts.getPrefer(this,Consts.KEY_STATUSNOTIC,true).toString())
        if (Utils.getPrefer(this,Utils.KEY_STATUSNOTIC,true).toString()=="true"){
            if (notification!=null){
//                FileHelper.saveToFile("onMessageReceived if: $data")
//                FileHelper.saveToFile("onMessageReceived StatusNotic if:" + Consts.getPrefer(this,Consts.KEY_STATUSNOTIC,true).toString())
                sendNotification(notification.body!!,data)
            }else{
//                FileHelper.saveToFile("onMessageReceived else: "+message.data["msg"]!!)
//                FileHelper.saveToFile("onMessageReceived StatusNotic else:" + Consts.getPrefer(this,Consts.KEY_STATUSNOTIC,true).toString())
                sendNotification(message.data["msg"]!!,data)
            }

        }

    }
    private fun sendNotification(messageBody:String, data:Map<String,String>){
//        FileHelper.saveToFile("sendNotification StatusNotic:" + Consts.getPrefer(this,Consts.KEY_STATUSNOTIC,true).toString())
//        FileHelper.saveToFile("sendNotification: $messageBody")
        val id = (System.currentTimeMillis()/60000).toInt()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel-" + getString(R.string.app_name)
        val icon = R.mipmap.ic_launcher
        val title = getString(R.string.app_name)
        val GROUP_KE = packageName
        val iconBitmap = BitmapFactory.decodeResource(resources, icon)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val mChannel = NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
            mChannel.enableVibration(true)
            mChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,audioAttributes)
            notificationManager.createNotificationChannel(mChannel)
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_IMMUTABLE)
        val mBuilder = NotificationCompat.Builder(this,channelId)
        val noti = mBuilder.setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(messageBody)
            .setContentIntent(pIntent)
            .setLargeIcon(iconBitmap)
            .setGroup(GROUP_KE)
            .setColor(0xf2fc605)
            .build()

        noti.flags = noti.flags or Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(id,noti)
//        FileHelper.saveToFile("sendNotification title: $title")
    }
}