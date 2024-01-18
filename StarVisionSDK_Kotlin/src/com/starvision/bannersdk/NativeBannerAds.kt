package com.starvision.bannersdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.starvision.AppPreferences
import com.starvision.Const
import com.starvision.api.LoadData
import com.starvision.bannersdk.databinding.NativeBannerAdsMiddleBinding
import com.starvision.bannersdk.databinding.NativeBannerAdsSmallBinding
import com.starvision.bannersdk.databinding.NativeBannerAdsSmallNewBinding
import com.starvision.models.AdsModel
import java.text.SimpleDateFormat

class NativeBannerAds @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val mContext = context
    private val loadData = LoadData(context)
    private val appPreferences = AppPreferences
    private val tagS = this.javaClass.simpleName
    private var linear : LinearLayout? = null
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    private val bindingSmall : NativeBannerAdsSmallBinding by lazy { NativeBannerAdsSmallBinding.inflate(LayoutInflater.from(context)) }
    private val bindingMedium : NativeBannerAdsMiddleBinding by lazy { NativeBannerAdsMiddleBinding.inflate(LayoutInflater.from(context)) }
    private val bindingSmallVer : NativeBannerAdsSmallNewBinding by lazy { NativeBannerAdsSmallNewBinding.inflate(LayoutInflater.from(context)) }

    private lateinit var mBannerAdsListener : BannerAdsListener

    interface BannerAdsListener{
        fun onSuccess(strJson : String)
        fun onOtherAds(strAdsvertisingName : String)
        fun onBannerClick(strJson : String)
        fun onFailed(strErrorMessage : String)
    }
    fun setBannerAdsListener(listener : BannerAdsListener){
        mBannerAdsListener =  listener
    }
    fun loadAds(strPage : String, strUUID : String, sizeInt : Int){
//        Const.log(tagS,"loadAds")
        try {
//            Const.log(tagS,"try")
//            Const.log(tagS,"strUUID : $strUUID")
            val timeNow = dateFormat.format(System.currentTimeMillis())
            val timeData = dateFormat.parse((appPreferences.getPreferences(mContext,AppPreferences.KEY_PREFS_TIME_LOADS,"")).toString())
            val result : Long =  (dateFormat.parse(timeNow)!!.time - timeData!!.time)
            if(loadData.checkData == false){
//                Const.log(tagS,"loadAds : if = โหลด Api ไม่ได้")
                mBannerAdsListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)

            }else if (result > (10*  60*  1000)){
//                Const.log(tagS,"loadAds : else if = เกิน10นาที โหลด API ใหม่")
                loadData.loadAdsData(true)
                val newdataApi = appPreferences.getPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA,"")
                setApiToView(newdataApi.toString(),strPage,sizeInt)

            }else{
//                Const.log(tagS,"loadAds : else = ยังไม่เกิน10นาที ใช้ Json Preferences")
                val dataApi = appPreferences.getPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA,"")
                val adsModel = Gson().fromJson(dataApi.toString(), AdsModel::class.java)
                val dataRowPageConfig = adsModel.Datarowpageconfig
                for (i in adsModel.Datarowpageconfig.indices) {
                    if (adsModel.Datarowpageconfig[i].Apppageno.toInt() == strPage.toInt()) {
                        if (dataRowPageConfig[i].Mobileadvertisingname == MobileAdvertising.ADVERTISING_CLOSE) {
//                            Const.log(tagS, "loadAds : else = CLOSE")
                            mBannerAdsListener.onFailed(MobileAdvertising.ADVERTISING_CLOSE)
                        } else {
                            setApiToView(dataApi.toString(), strPage, sizeInt)
                        }
                    }
                }
//                val dataRowPageConfig = adsModel.Datarowpageconfig
//                if (dataRowPageConfig[strPage.toInt()].Mobileadvertisingname == MobileAdvertising.ADVERTISING_CLOSE) {
//                    Const.log(tagS,"loadAds : else = CLOSE")
//                    mBannerAdsListener.onFailed(MobileAdvertising.ADVERTISING_CLOSE)
//                } else
//                    setApiToView(dataApi.toString(),strPage,sizeInt)
            }
        }catch (e : Exception){
//            Const.log(tagS,"catch")
//            Const.log(tagS,"loadAds : catch")
            val dataApi = appPreferences.getPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA,"")
            val adsModel = Gson().fromJson(dataApi.toString(), AdsModel::class.java)
            for (i in adsModel.Datarowpageconfig.indices) {
                if (adsModel.Datarowpageconfig[i].Apppageno.toInt() == strPage.toInt()) {
                    if (adsModel.Datarowpageconfig[i].Mobileadvertisingname == MobileAdvertising.ADVERTISING_CLOSE) {
                        mBannerAdsListener.onFailed(MobileAdvertising.ADVERTISING_CLOSE)
                    } else {
                        mBannerAdsListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
                    }
                }
            }
        }
    }

//    fun loadAds(strPage : String, strUUID : String, sizeInt : Int){
//        Const.log(tagS,"loadAds")
//        try {
//            Const.log(tagS,"try")
//            Const.log(tagS,"strUUID : $strUUID")
//            val timeNow = dateFormat.format(System.currentTimeMillis())
//            val timeData = dateFormat.parse((appPreferences.getPreferences(mContext,AppPreferences.KEY_PREFS_TIME_LOADS,"")).toString())
//            val result : Long =  (dateFormat.parse(timeNow)!!.time - timeData!!.time)
//            if(loadData.checkData == false){
//                Const.log(tagS,"loadAds : if = โหลด Api ไม่ได้")
//                mBannerAdsListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
//
//            }else if (result > (10 * 60 * 1000)){
//                Const.log(tagS,"loadAds : else if = เกิน10นาที โหลด API ใหม่")
//                loadData.loadAdsData(true)
//                val newdataApi = appPreferences.getPreferences(mContext,
//                    AppPreferences.KEY_PREFS_JSON_DATA,"")
//                setApiToView(newdataApi.toString(),strPage,sizeInt)
//
//            }else{
//                Const.log(tagS,"loadAds : else = ยังไม่เกิน10นาที ใช้ Json Preferences")
//                val dataApi = appPreferences.getPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA,"")
//                val adsModel = Gson().fromJson(dataApi.toString(), AdsModel::class.java)
//                val dataRowPageConfig = adsModel.Datarowpageconfig
//                for (i in dataRowPageConfig.indices) {
//                    val dataPage = dataRowPageConfig[i]
//                    if (dataPage.Mobileadvertisingname == MobileAdvertising.ADVERTISING_CLOSE) {
//                        Const.log(tagS,"loadAds : else = CLOSE")
//                        mBannerAdsListener.onFailed(MobileAdvertising.ADVERTISING_CLOSE)
//                    } else
//                        setApiToView(dataApi.toString(),strPage,sizeInt)
//                }
//            }
//        }catch (e : Exception){
//            Const.log(tagS,"catch")
//            Const.log(tagS,"loadAds : catch")
//            val dataApi = appPreferences.getPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA,"")
//            val adsModel = Gson().fromJson(dataApi.toString(), AdsModel::class.java)
//            val dataRowPageConfig = adsModel.Datarowpageconfig
//            for (i in dataRowPageConfig.indices) {
//                val dataPage = dataRowPageConfig[i]
//                if (dataPage.Mobileadvertisingname == MobileAdvertising.ADVERTISING_CLOSE) {
//                    mBannerAdsListener.onFailed(MobileAdvertising.ADVERTISING_CLOSE)
//                } else
//                    mBannerAdsListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
//            }
//        }
//    }

    private val NATIVE_BANNER_SMALL_HORIZONTAL = 0
    private val NATIVE_BANNER_MEDIUM_HORIZONTAL = 1
    private val NATIVE_BANNER_SMALL_VERTICAL = 2

    @SuppressLint("ClickableViewAccessibility")
    fun setApiToView(dataApi: String, strPage: String, sizeInt : Int) {
        try {
            val adsModel = Gson().fromJson(dataApi, AdsModel::class.java)
            if(adsModel.Status == "True"){
//                Const.log(tagS,"setApiToView")
                val dataRowPageConfig = adsModel.Datarowpageconfig
                for (i in dataRowPageConfig.indices){
                    val dataPage = dataRowPageConfig[i]
//                    Const.log(tagS, "setApiToView dataPage :$dataPage")
                    if(strPage == dataPage.Apppageno && dataPage.Apppageshow == "True"){
//                        Const.log(tagS,"setApiToView True")
                        if (dataPage.Mobileadvertisingname == MobileAdvertising.ADVERTISING_STARVISION
                            || dataPage.Mobileadvertisingname == MobileAdvertising.ADVERTISING_WINNER) {

                            val dataRowPageBanner = adsModel.Datarowpageconfig[i].Datarowpagebanner
                            for (a in dataRowPageBanner.indices){
                                val randomBannerIndex = (Math.random() * dataRowPageBanner.size).toInt()
                                val pageBanner = dataRowPageBanner[randomBannerIndex]

                                val dot = "."
                                val arr = pageBanner.Multilink.split(dot).toTypedArray()
//                                Const.log(tagS,"Image Link arr1 : "+arr[1])
//                                Const.log(tagS,"Image Link arr2 : "+arr[2])
//                                Const.log(tagS,"Image Link arr3 : "+arr[3])

                                val displayMetrics = resources.displayMetrics
                                val popupWidth = displayMetrics.widthPixels
                                val iSizeImageHeight = 120.0
                                val iSizeImageWidth = 640.0
                                val dblPercen = (popupWidth / iSizeImageWidth) * 100.toDouble() / 100.toDouble()
                                val iHeight = (dblPercen * iSizeImageHeight).toInt()

                                layoutParams.width = popupWidth
                                linear = this
                                (linear as NativeBannerAds).removeAllViews()

                                when (sizeInt) {
                                    NATIVE_BANNER_SMALL_HORIZONTAL -> {
                                        (linear as NativeBannerAds).addView(bindingSmall.root)
                                        bindingSmall.root.layoutParams.width = popupWidth
                                        Glide.with(mContext).load(pageBanner.Appbannericonurl).into(bindingSmall.mIvAppIcon)
                                        bindingSmall.mIvAppIcon.scaleType = ImageView.ScaleType.FIT_XY
                                        bindingSmall.mTvAppName.text = pageBanner.Appbannername

                                        bindingSmall.mTvAppName.setOnClickListener {
                                            bindingSmall.mTvAppName.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmall.mTvAppName.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmall.mTvAppName.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                        bindingSmall.mIvAppIcon.setOnTouchListener { v, event ->
                                            when (event?.action) {
                                                MotionEvent.ACTION_DOWN -> bindingSmall.mIvAppIcon.setColorFilter(R.color.black_trin_image)
                                                MotionEvent.ACTION_UP -> bindingSmall.mIvAppIcon.clearColorFilter()
                                                MotionEvent.ACTION_CANCEL -> bindingSmall.mIvAppIcon.clearColorFilter()
                                            }
                                            v?.onTouchEvent(event) ?: true
                                        }
                                        bindingSmall.mIvAppIcon.setOnClickListener {
                                            bindingSmall.mIvAppIcon.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmall.mIvAppIcon.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmall.mIvAppIcon.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                        bindingSmall.mBtInstall.setOnClickListener {
                                            bindingSmall.mBtInstall.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmall.mBtInstall.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmall.mBtInstall.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                    }
                                    NATIVE_BANNER_MEDIUM_HORIZONTAL -> {
                                        (linear as NativeBannerAds).addView(bindingMedium.root)
                                        bindingMedium.root.layoutParams.width = popupWidth
                                        bindingMedium.mIvBanner.layoutParams.height = iHeight - 10
                                        Glide.with(mContext).load(pageBanner.Appbannericonurl).into(bindingMedium.mIvAppIcon)

                                        if(arr[3] == "gif"){
//                                            Const.log(tagS,"gif function")
                                            Glide.with(mContext).asGif().load(pageBanner.Multilink).into(bindingMedium.mIvBanner)
                                        }else {
//                                            Const.log(tagS,"jpeg function")
                                            Glide.with(mContext).load(pageBanner.Multilink).into(bindingMedium.mIvBanner)
                                        }

                                        bindingMedium.mIvAppIcon.scaleType = ImageView.ScaleType.FIT_XY
                                        bindingMedium.mTvAppName.text = pageBanner.Appbannername

                                        bindingMedium.mTvAppName.setOnClickListener {
                                            bindingMedium.mTvAppName.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mTvAppName.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mTvAppName.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                        bindingMedium.mIvAppIcon.setOnTouchListener { v, event ->
                                            when (event?.action) {
                                                MotionEvent.ACTION_DOWN -> bindingMedium.mIvAppIcon.setColorFilter(R.color.black_trin_image)
                                                MotionEvent.ACTION_UP -> bindingMedium.mIvAppIcon.clearColorFilter()
                                                MotionEvent.ACTION_CANCEL -> bindingMedium.mIvAppIcon.clearColorFilter()
                                            }
                                            v?.onTouchEvent(event) ?: true
                                        }
                                        bindingMedium.mIvAppIcon.setOnClickListener {
                                            bindingMedium.mIvAppIcon.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mIvAppIcon.isEnabled = true
                                                    },100)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mIvAppIcon.isEnabled = true
                                                    },100)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                        bindingMedium.mBtInstall.setOnClickListener {
                                            bindingMedium.mBtInstall.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mBtInstall.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mBtInstall.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                        bindingMedium.mIvBanner.setOnTouchListener { v, event ->
                                            when (event?.action) {
                                                MotionEvent.ACTION_DOWN -> bindingMedium.mIvBanner.setColorFilter(R.color.black_trin_image)
                                                MotionEvent.ACTION_UP -> bindingMedium.mIvBanner.clearColorFilter()
                                                MotionEvent.ACTION_CANCEL -> bindingMedium.mIvBanner.clearColorFilter()
                                            }
                                            v?.onTouchEvent(event) ?: true
                                        }
                                        bindingMedium.mIvBanner.setOnClickListener {
                                            bindingMedium.mIvBanner.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mIvBanner.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingMedium.mIvBanner.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                    }
                                    NATIVE_BANNER_SMALL_VERTICAL -> {
                                        (linear as NativeBannerAds).addView(bindingSmallVer.root)
                                        bindingSmallVer.root.layoutParams.width = popupWidth
                                        Glide.with(mContext).load(pageBanner.Appbannericonurl).into(bindingSmallVer.mIvAppIcon)
                                        bindingSmallVer.mIvAppIcon.scaleType = ImageView.ScaleType.FIT_XY
                                        bindingSmallVer.mTvAppName.text = pageBanner.Appbannername

                                        bindingSmallVer.mTvAppName.setOnClickListener {
                                            bindingSmallVer.mTvAppName.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmallVer.mTvAppName.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmallVer.mTvAppName.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                        bindingSmallVer.mIvAppIcon.setOnTouchListener { v, event ->
                                            when (event?.action) {
                                                MotionEvent.ACTION_DOWN -> bindingSmallVer.mIvAppIcon.setColorFilter(R.color.black_trin_image)
                                                MotionEvent.ACTION_UP -> bindingSmallVer.mIvAppIcon.clearColorFilter()
                                                MotionEvent.ACTION_CANCEL -> bindingSmallVer.mIvAppIcon.clearColorFilter()
                                            }
                                            v?.onTouchEvent(event) ?: true
                                        }
                                        bindingSmallVer.mIvAppIcon.setOnClickListener {
                                            bindingSmallVer.mIvAppIcon.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmallVer.mIvAppIcon.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmallVer.mIvAppIcon.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                        bindingSmallVer.mBtInstall.setOnClickListener {
                                            bindingSmallVer.mBtInstall.isEnabled = false
                                            if (pageBanner.Multilinkurlbrowser != "") {
                                                if(pageBanner.Multilinkurlbrowser.contains("http")){
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmallVer.mBtInstall.isEnabled = true
                                                    },1000)
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                                                    mContext.startActivity(intent)
                                                }else{
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        bindingSmallVer.mBtInstall.isEnabled = true
                                                    },1000)
                                                    Const.openPlayStore(mContext, pageBanner.Multilinkurlbrowser)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            mBannerAdsListener.onSuccess(dataRowPageBanner.toString())
                        }else{
                            mBannerAdsListener.onOtherAds(dataPage.Mobileadvertisingname)
                        }
                        break
                    }
                }
            }
        }catch (e : Exception){
            mBannerAdsListener.onFailed("Load Failed")
            mBannerAdsListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
        }
    }
}