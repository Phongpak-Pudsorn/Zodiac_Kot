package com.smileapp.zodiac.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

object Utils {
    private val namePreferences = "zdaadmob"
    private val modePreferences = Context.MODE_PRIVATE
    private var sharedPrefs: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null
    const val KEY_STATUS_PUSNOTIFICATION = "Status"
    const val APP_SHARED_PREFS = "mAppPreferences"
    const val KEY_STATUSNOTIC = "KEY_STATUSNOTIC"
    const val KEY_ACCEPT = "isAccept"
    const val KEY_FIRST_OPEN = "isFirstTime"
    const val NAME_USER = "NAME_USER"
    const val KEY_RASI = "KEY_RASI"
    const val KEY_GENDER = "KEY_GENDER"
    const val NAME_DATE_RASI = "NAME_DATE_RASI"
    const val KEY_NOTICEADS = "KEY_NOTICEADS"
    const val ADVERTISING_ID_CLIENT = "ADVERTISING_ID_CLIENT"
    const val NAME_MENU_MONTH ="NAME_MENU_MONTH"
    const val NAME_MENU_YEAR ="NAME_MENU_YEAR"
    const val KEY_OPEN_PROFILE ="KEY_OPEN_PROFILE"
    const val KEY_SHARED_DAY ="KEY_SHARED_DAY"
    const val KEY_SHARED_ID ="KEY_SHARED_ID"
    const val KEY_SHARED_IMAGE ="KEY_SHARED_IMAGE"
    const val KEY_SHARED_DESC ="KEY_SHARED_DESC"
    var UUID = ""
    var currentFragment = 0

    fun AppPreference(context: Context) {
//    	Log.e("TAG", APP_SHARED_PREFS);
        sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE)
        prefsEditor = sharedPrefs!!.edit()

    }
    fun getStatusPushNoticfication(): Boolean? {
        return sharedPrefs!!.getBoolean(KEY_STATUS_PUSNOTIFICATION, false)
    }
    fun setStatusNotic(status:Boolean){
        prefsEditor!!.putBoolean(KEY_STATUSNOTIC,status)
        prefsEditor!!.commit()
    }
    fun getStatusNotic(): Boolean {
        return sharedPrefs!!.getBoolean(KEY_STATUSNOTIC,true)
    }
    fun setNameUser(name:String){
        prefsEditor!!.putString(NAME_USER,name)
        prefsEditor!!.commit()

    }
    fun getNameUser():String{
        return sharedPrefs!!.getString(NAME_USER,"").toString()
    }

    //---------END  BANNER POPUP  SIZE-------------------
    fun setAdvertisingIdClient(id: String?) {
        prefsEditor!!.putString(ADVERTISING_ID_CLIENT, id)
        prefsEditor!!.commit()
    }

    fun getAdvertisingIdClient(): String? {
        return sharedPrefs!!.getString(ADVERTISING_ID_CLIENT, "")
    }
    //---------END AdvertisingIdClient ID -------------------


    //--------- AdvertisingIdClient ID -------------------
    fun loadFromAssets(mContext: Context): String? {
        var dataStr = ""
        try {
            val input = mContext.assets.open("DataZodiac/Data_Zodiac.json")
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            dataStr = String(buffer, charset("UTF-8"))

        }catch (ex: IOException){
            ex.printStackTrace()
            return null
        }
        return dataStr
    }

    //---------END  SET ID RASI-------------------
    fun setRasi(month: Int) {
        prefsEditor!!.putInt(KEY_RASI, month)
        prefsEditor!!.commit()
    }

    fun getRasi(): Int {
        return sharedPrefs!!.getInt(KEY_RASI, 0)
    }
    fun setGENDER(Gender: String?) {
        prefsEditor!!.putString(KEY_GENDER, Gender)
        prefsEditor!!.commit()
    }

    fun getGENDER(): String? {
        return sharedPrefs!!.getString(KEY_GENDER, "")
    }

    //---------END  SET Name User-------------------

    fun setNameAndDateRasi(date: String?) {
        prefsEditor!!.putString(NAME_DATE_RASI, date)
        prefsEditor!!.commit()
    }

    fun getNameAndDateRasi(): String? {
        return sharedPrefs!!.getString(NAME_DATE_RASI, "")
    }
    fun setOpenProfile(status: Boolean){
        prefsEditor!!.putBoolean(KEY_OPEN_PROFILE,status)
        prefsEditor!!.commit()
    }
    fun getOpenProfile(): Boolean {
        return sharedPrefs!!.getBoolean(KEY_OPEN_PROFILE,true)
    }
    fun setNoticeAds(status: Boolean){
        prefsEditor!!.putBoolean(KEY_NOTICEADS,status)
        prefsEditor!!.commit()
    }
    fun getNoticeAds(): Boolean {
        return sharedPrefs!!.getBoolean(KEY_NOTICEADS,true)
    }
    fun setNameMenuMonth(name:String){
        prefsEditor!!.putString(NAME_MENU_MONTH,name)
        prefsEditor!!.commit()
    }
    fun getNameMenuMonth():String? {
        return sharedPrefs!!.getString(NAME_MENU_MONTH,"")
    }
    fun setNameMenuYear(name:String){
        prefsEditor!!.putString(NAME_MENU_YEAR,name)
        prefsEditor!!.commit()
    }
    fun getNameMenuYear():String? {
        return sharedPrefs!!.getString(NAME_MENU_YEAR,"")
    }
    fun setSharedDay(name: String){
        prefsEditor!!.putString(KEY_SHARED_DAY,name)
        prefsEditor!!.commit()
    }
    fun getSharedDay():String?{
        return sharedPrefs!!.getString(KEY_SHARED_DAY,"")
    }
    fun setSharedID(id: Int){
        prefsEditor!!.putInt(KEY_SHARED_ID,id)
        prefsEditor!!.commit()
    }
    fun getSharedID():Int{
        return sharedPrefs!!.getInt(KEY_SHARED_ID,0)
    }
    fun setSharedImage(name: String){
        prefsEditor!!.putString(KEY_SHARED_IMAGE,name)
        prefsEditor!!.commit()
    }
    fun getSharedImage():String?{
        return sharedPrefs!!.getString(KEY_SHARED_IMAGE,"")
    }
    fun setSharedDesc(name: String){
        prefsEditor!!.putString(KEY_SHARED_DESC,name)
        prefsEditor!!.commit()
    }
    fun getSharedDesc():String?{
        return sharedPrefs!!.getString(KEY_SHARED_DESC,"")
    }


    @Throws(Exception::class)
    fun doHttpUrlConnectionAction(desiredUrl: String?): String? {
        var url: URL? = null
        var reader: BufferedReader? = null
        val stringBuilder: StringBuilder
        var strHttp = ""
        return try {
            // create the HttpURLConnection
            url = URL(desiredUrl)
            val connection = url.openConnection() as HttpsURLConnection
            // just want to do an HTTP GET here
            connection.requestMethod = "GET"
            // connection.setReadTimeout(5000);
            connection.connectTimeout = 10000
            connection.connect()
            // read the output from the server
            reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
            stringBuilder = StringBuilder()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(
                    """
                        $line
                        
                        """.trimIndent()
                )
            }
            strHttp = stringBuilder.toString()
            strHttp
        } catch (e: Exception) {
            Log.e("timeout", "timeout")
            strHttp = ""
            e.printStackTrace()
            strHttp
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }
        }
    }
    fun setPrefer(mcontext: Context, key:String, objects:Any){
        val preferences = mcontext.getSharedPreferences(namePreferences, modePreferences)
        val prefer = preferences.edit()
        when(objects){
            is String -> {
//                Log.e("encoded","before: "+objects)
                val encoded: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encodeToString(objects.toByteArray())
                } else {
                    android.util.Base64.encodeToString(objects.toByteArray(),0)
                }
//                Log.e("encoded","after: "+encoded)
                prefer.putString(key,encoded)
            }
            is Int -> {
                prefer.putInt(key,objects)
            }
            is Boolean -> {
                prefer.putBoolean(key,objects)
            }
            is Float -> {
                prefer.putFloat(key,objects)
            }
            is Long -> {
                prefer.putLong(key,objects)
            }
        }
        prefer.apply()
    }
    fun getPrefer(mcontext: Context, key: String, defaultObj:Any):Any?{
        val preferences = mcontext.getSharedPreferences(namePreferences, modePreferences)
        when(defaultObj){
            is String -> {
                val prefer = preferences.getString(key,defaultObj)
                val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String(Base64.getDecoder().decode(prefer.toString()))
                } else {
                    android.util.Base64.decode(prefer,0).toString()
                }
                return  decoded
            }
            is Int -> {
                return  preferences.getInt(key,defaultObj)
            }
            is Boolean -> {
                return  preferences.getBoolean(key,defaultObj)
            }
            is Float -> {
                return  preferences.getFloat(key,defaultObj)
            }
            is Long -> {
                return  preferences.getLong(key,defaultObj)
            }
        }
        return null
    }
    fun getHtmlData(
        context: Context?,
        data: String,
        size: Int,
    ): String? {
        var head = ""
        head =
            "<head><style>@font-face{font-family: 'psl029pro';src: url('file:///android_asset/fonts/psl029pro.ttf');} body {font-family: 'psl029pro'; font-size:$size; color: #000000; }</style></head>"
        return "<html>$head<body>$data</body></html>"
    }

    fun getHtmlData_While(
        context: Context?,
        data: String,
        size: Int,
    ): String? {
        var head = ""
        head =
            "<head><style>@font-face{font-family: 'psl029pro';src: url('file:///android_asset/fonts/psl029pro.ttf');} body {font-family: 'psl029pro'; font-size:$size; color: #FFFFFF; }</style></head>"
        return "<html>$head<body>$data</body></html>"
    }
    fun setTextGradient_Blue(tv:TextView){
        val myShader: Shader = LinearGradient(
            0f, 70f, 0f, 180f,
            Color.WHITE, Color.BLUE,
            Shader.TileMode.CLAMP
        )
        tv.paint.shader = myShader
    }
    fun setTextGradient_Green(tv:TextView){
        val myShader: Shader = LinearGradient(
            0f, 50f, 0f, 200f,
            Color.WHITE, Color.GREEN,
            Shader.TileMode.CLAMP
        )
        tv.paint.shader = myShader
    }
    fun setStringImageDrawable(
        context: Context,
        img: ImageView,
        imgTarot: String?,
        reqWidth: Int,
        reqHeight: Int,
    ) {
        try {
            val imageResourceId =
                context.resources.getIdentifier(imgTarot, "mipmap", context.packageName)
            val bitmap: Bitmap? = decodeSampledBitmapFromResource(
                context.resources,
                imageResourceId,
                reqWidth,
                reqHeight
            )
            img.setImageBitmap(bitmap)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    fun decodeSampledBitmapFromResource(
        res: Resources?,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int,
    ): Bitmap? {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(
            options, reqWidth,
            reqHeight
        )

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }


}