package com.starvision.bannersdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.starvision.AppPreferences
import com.starvision.Const
import com.starvision.api.LoadData
import com.starvision.models.AdsModel
import java.text.SimpleDateFormat

class BannerAds @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val mContext = context
    private val loadData = LoadData(context)
    private val appPreferences = AppPreferences
    private val tagS = this.javaClass.simpleName
    private var bannerAds : AppCompatImageView? = null
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    private lateinit var mBannerListener : BannerAdsListener
    interface BannerAdsListener {
        fun onSuccess(strJson : String?)
        fun onOtherAds(strAdsvertisingName : String?)
        fun onBannerClick(strJson : String?)
        fun onFailed(strErrorMessage : String?)
    }
    fun setBannerAdsListener(listener : BannerAdsListener) {
        mBannerListener = listener
    }

    fun loadAds (strPage : String, strUUID : String){
        Const.log(tagS,"loadAds")
        try {
            Const.log(tagS,"try")
            Const.log(tagS,"strUUID : $strUUID")
            val timeNow = dateFormat.format(System.currentTimeMillis())
            val timeData = dateFormat.parse((appPreferences.getPreferences(mContext,AppPreferences.KEY_PREFS_TIME_LOADS,"")).toString())
            val result : Long =  (dateFormat.parse(timeNow)!!.time - timeData!!.time)
            Const.log(tagS, "TimeNow : $timeNow")
            Const.log(tagS, "result : $result")
            if(loadData.checkData == false){
                Const.log(tagS,"loadAds : if = โหลด Api ไม่ได้")
                mBannerListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)

            }else if (result > (10 * 60 * 1000)){
                Const.log(tagS,"loadAds : else if = เกิน10นาที โหลด API ใหม่")
                loadData.loadAdsData(true)
                val newdataApi = appPreferences.getPreferences(mContext,
                    AppPreferences.KEY_PREFS_JSON_DATA,"")
                setApiToView(newdataApi.toString(),strPage)

            }else{
                Const.log(tagS,"loadAds : else = ยังไม่เกิน10นาที ใช้ Json Preferences")
                val dataApi = appPreferences.getPreferences(mContext,AppPreferences.KEY_PREFS_JSON_DATA,"")
                setApiToView(dataApi.toString(),strPage)
            }
        }catch (e : Exception){
            Const.log(tagS,"catch")
            Const.log(tagS,"loadAds : catch")
            mBannerListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setApiToView(dataApi : String, strPage : String){
        try {
            val adsModel = Gson().fromJson(dataApi, AdsModel::class.java)
            if(adsModel.Status == "True"){
                val dataRowPageConfig = adsModel.Datarowpageconfig
                for (i in dataRowPageConfig.indices){
                    val dataPage = dataRowPageConfig[i]
                    if(strPage == dataPage.Apppageno && dataPage.Apppageshow == "True"){

                        if (dataPage.Mobileadvertisingname == MobileAdvertising.ADVERTISING_STARVISION
                            || dataPage.Mobileadvertisingname == MobileAdvertising.ADVERTISING_WINNER) {

                            val dataRowPageBanner = adsModel.Datarowpageconfig[i].Datarowpagebanner
                            for (a in dataRowPageBanner.indices){
                                val randomBannerIndex = (Math.random() * dataRowPageBanner.size).toInt()
                                val pageBanner = dataRowPageBanner[randomBannerIndex]
                                val displayMetrics = resources.displayMetrics

                                val popupWidth : Int = (displayMetrics.widthPixels)
                                val iSizeImageHeight = 90.toDouble()
                                val iSizeImageWidth = 640.toDouble()
                                val dblPercen = (popupWidth / iSizeImageWidth) * (100) / (100).toDouble()
                                val iHeight = (dblPercen * (iSizeImageHeight)).toInt()
                                layoutParams.height = iHeight
                                layoutParams.width = popupWidth
                                scaleType = ScaleType.FIT_XY
                                bannerAds = this

                                val dot = "."
                                val arr = pageBanner.Multilink.split(dot).toTypedArray()
                                Const.log(tagS,"Image Link arr1 : "+arr[1])
                                Const.log(tagS,"Image Link arr2 : "+arr[2])
                                Const.log(tagS,"Image Link arr3 : "+arr[3])

                                if(arr[3] == "gif"){
                                    Const.log(tagS,"gif function")
                                    Glide.with(mContext).asGif().load(pageBanner.Multilink).into(bannerAds!!)
                                }else {
                                    Const.log(tagS,"jpeg function")
                                    Glide.with(mContext).load(pageBanner.Multilink)
                                        .into(bannerAds!!)
                                }

                                (bannerAds as BannerAds).setOnTouchListener { v, event ->
                                    when (event?.action) {
                                        MotionEvent.ACTION_DOWN -> (bannerAds as BannerAds).setColorFilter(R.color.black_trin_image)
                                        MotionEvent.ACTION_UP -> (bannerAds as BannerAds).clearColorFilter()
                                        MotionEvent.ACTION_CANCEL -> (bannerAds as BannerAds).clearColorFilter()
                                    }
                                    v?.onTouchEvent(event) ?: true
                                }

                                (bannerAds as BannerAds).setOnClickListener {
                                    if (pageBanner.Multilinkurlbrowser != "") {
                                        (bannerAds as BannerAds).isEnabled = false
                                        if(pageBanner.Multilinkurlbrowser.contains("http")){
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                (bannerAds as BannerAds).isEnabled = true },1000)
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                            mContext.startActivity(intent)
                                        }else{
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                (bannerAds as BannerAds).isEnabled = true },1000)
                                            Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                        }
                                    }
                                }
                            }
                            mBannerListener.onSuccess(dataRowPageBanner.toString())
                        }else{
                            Const.log(tagS,"onOtherAds : Mobileadvertisingname : "+dataPage.Mobileadvertisingname)
                            mBannerListener.onOtherAds(dataPage.Mobileadvertisingname)
                        }
                        break
                    }
                }
            }
        }catch (e : Exception){
            Const.log(tagS,"setApiToView catch")
            mBannerListener.onFailed("Load Failed")
            mBannerListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
        }
    }
}