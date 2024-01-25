package com.starvision.bannersdk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.starvision.AppPreferences
import com.starvision.Config
import com.starvision.Const
import com.starvision.api.LoadData
import com.starvision.bannersdk.databinding.PopupBannerBigDialogBinding
import com.starvision.bannersdk.databinding.PopupVdoDialogBinding
import com.starvision.models.AdsModel
import java.text.SimpleDateFormat

class PopupAds (context: Context) {

    private val bindingBig : PopupBannerBigDialogBinding by lazy { PopupBannerBigDialogBinding.inflate(LayoutInflater.from(context)) }
    private val bindingVDO : PopupVdoDialogBinding by lazy { PopupVdoDialogBinding.inflate(LayoutInflater.from(context)) }

    private val mContext = context
    private val mActivity = (mContext as Activity)
    private val loadData = LoadData(context)
    private val appPreferences = AppPreferences
    private val tagS = this.javaClass.simpleName
    private var popupDialog : Dialog? = null
    private var bPopupVdo = false
    private var bSkip = false
    private var bReadyToShow = false
    private var intAudio = 0
    private var bPrepare = false
    private var mTimerLoadVideo : CountDownTimer? = null
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("HH:mm:ss")
    private var timeSkip = ""
    private var currentPosition = 0

    private lateinit var mPopupListener : PopupAdsListener
    interface PopupAdsListener {
        fun onSuccess(strJson : String)
        fun onOtherAds(strAdsvertisingName : String)
        fun onClose()
        fun onFailed(strErrorMessage : String)
        fun onBannerClick(strJson : String)
    }
    fun setPopupAdsListener(listener : PopupAdsListener){
        mPopupListener = listener
    }

    fun loadAds (strPage : String?, strUUID : String?){
        Const.log(tagS,"loadAds")
        try {
            if (strPage != null) {
                Const.log(tagS, "strUUID : $strUUID")
                val timeNow = dateFormat.format(System.currentTimeMillis())
                val timeData = dateFormat.parse(
                    (appPreferences.getPreferences(
                        mContext,
                        AppPreferences.KEY_PREFS_TIME_LOADS,
                        ""
                    )).toString()
                )
                val result: Long = (dateFormat.parse(timeNow)!!.time - timeData!!.time)
                if (result > (10 * 60 * 1000)) {
                    Const.log(tagS, "loadAds : if = เกิน10นาที โหลด API ใหม่")
                    loadData.loadAdsData(true)
                    val newDataApi = appPreferences.getPreferences(
                        mContext,
                        AppPreferences.KEY_PREFS_JSON_DATA,
                        ""
                    )
                    setApiToView(newDataApi.toString(), strPage)

                } else if (loadData.checkData == false) {
                    Const.log(tagS, "loadAds : else if = โหลด Api ไม่ได้")
                    mPopupListener.onFailed("Load Api Failed")
                    mPopupListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
                } else {
                    Const.log(tagS, "loadAds : else = ยังไม่เกิน10นาที ใช้ Json Preferences")
                    val dataApi = appPreferences.getPreferences(
                        mContext,
                        AppPreferences.KEY_PREFS_JSON_DATA,
                        ""
                    )
                    setApiToView(dataApi.toString(), strPage)
                }
            }else{
                Const.log(tagS,"AppOpen : ")
                mPopupListener.onFailed("Load Api Failed")
                mPopupListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
            }
        }catch (e : Exception){
            Const.log(tagS,"catch : "+loadData.checkData)
            Const.log(tagS,"loadAds : catch = โหลด Api ไม่ได้")
            mPopupListener.onFailed("Load Api Failed")
            mPopupListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
        }
    }

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
                            for (a in 0..dataRowPageBanner.size){
                                val pageBanner = dataRowPageBanner.random()

                                if(pageBanner.Linktype == "image_big") {
                                    Const.log(tagS,"Linktype : Image_BIG")
                                    setupPopupBig(pageBanner)

                                }else if (pageBanner.Linktype == "vdo"){
                                    Const.log(tagS,"Linktype : VDO")
                                    setupVDOPopup(pageBanner)

                                }
                                timeSkip = pageBanner.Multilinktimeskip
                            }
                            Const.log(tagS,"onSuccess")
                            mPopupListener.onSuccess(dataRowPageBanner.toString())
                        }else{
                            Const.log(tagS,"Load Failed")
                            mPopupListener.onFailed("Load Api Failed")
                            mPopupListener.onOtherAds(dataPage.Mobileadvertisingname)
                        }
                        break
                    }
                }
            }
        }catch (e : Exception){
            Const.log(tagS,"catch")
            mPopupListener.onFailed("Load Api Failed")
            mPopupListener.onOtherAds(MobileAdvertising.ADVERTISING_ADMOB)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPopupBig(listBanner: AdsModel.DatarowConfig.DatarowBanner) {
        try {
            Const.log(tagS,"setupBig :")
            bPopupVdo = false
            val displayMetrics = mContext.resources.displayMetrics
            val popupWidth = displayMetrics.widthPixels
            val popupHeight = displayMetrics.heightPixels
            val param = FrameLayout.LayoutParams(popupWidth, popupHeight - Const.getStatusBarHeight(mActivity))
            param.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
            bindingBig.mFlBanner.layoutParams = param

            bindingBig.mIvBannerPopup.setOnTouchListener { v, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> bindingBig.mIvBannerPopup.setColorFilter(R.color.black_trin_image)
                    MotionEvent.ACTION_UP -> bindingBig.mIvBannerPopup.clearColorFilter()
                    MotionEvent.ACTION_CANCEL -> bindingBig.mIvBannerPopup.clearColorFilter()
                }
                v?.onTouchEvent(event) ?: true
            }
            bindingBig.mIvBannerPopup.setOnClickListener {
                bindingBig.mIvBannerPopup.isEnabled = false
                if (listBanner.Multilinkurlbrowser != "") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        bindingBig.mIvBannerPopup.isEnabled = true
                    },1000)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(listBanner.Multilinkurlbrowser)
                    mContext.startActivity(intent)
                }
            }
            bindingBig.mBtClosePopUp.setOnClickListener {
                bindingBig.mIvBannerPopup.isEnabled = true
                mPopupListener.onClose()
                if (popupDialog != null) {
                    popupDialog!!.dismiss()
                }
            }
            if (listBanner.Multilink != "") {
                val dot = "."
                val arr = listBanner.Multilink.split(dot).toTypedArray()
                Const.log(tagS,"Image Link arr1 : "+arr[1])
                Const.log(tagS,"Image Link arr2 : "+arr[2])
                Const.log(tagS,"Image Link arr3 : "+arr[3])

                if(arr[3] == "gif"){
                    Const.log(tagS,"gif function")
                    Glide.with(mContext).asGif().load(listBanner.Multilink).into(bindingBig.mIvBannerPopup)
                }else {
                    Const.log(tagS,"jpeg function")
                    Glide.with(mContext).load(listBanner.Multilink)
                        .into(bindingBig.mIvBannerPopup)
                }

                popupDialog = Dialog(mContext, R.style.PopupDialog)
                if (bindingBig.root.parent != null){
                    (bindingBig.root.parent as ViewGroup).removeView(bindingBig.root)
                }
                popupDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                popupDialog!!.setContentView(bindingBig.root)
                popupDialog!!.setCancelable(true)
                popupDialog!!.setOnCancelListener { dialog ->
                    if (popupDialog != null) {
                        dialog.cancel()
                        mPopupListener.onClose()
                    }
                }
                bReadyToShow = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mPopupListener.onFailed("Load Failed")
        }
    }

    private fun setupVDOPopup(pageBanner: AdsModel.DatarowConfig.DatarowBanner) {
        try {
            Const.log(tagS,"setupVDO :")
            bPopupVdo = true
            bSkip = false
            bindingVDO.mCardSkip.isEnabled = false
            bindingVDO.mCardSkip.visibility = View.GONE
            bindingVDO.mIvVdoPoster.visibility = View.GONE
            bindingVDO.mBtClosePopUp.visibility = View.GONE
            bindingVDO.mIvPlay.visibility = View.GONE
            bindingVDO.mIvSound.visibility = View.VISIBLE

            val displayMetrics = mContext.resources.displayMetrics
            val popupWidth = displayMetrics.widthPixels
            val popupHeight = displayMetrics.heightPixels
            val param = FrameLayout.LayoutParams(popupWidth, popupHeight - Const.getStatusBarHeight(mActivity))
            param.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
            bindingVDO.mFlBanner.layoutParams = param

            if (pageBanner.Multilinkurlbrowser != "") {
                bindingVDO.mIvDownload.setImageResource(R.drawable.img_open_web)
            } else {
                bindingVDO.mIvDownload.setImageResource(R.drawable.img_download)
            }
            val audioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            intAudio = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

            if (intAudio == 0) {
                intAudio = 3
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, intAudio, 0)
            }

            bindingVDO.mIvSound.setOnClickListener {
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                    bindingVDO.mIvSound.setImageResource(R.drawable.img_sound_disable)
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, intAudio, 0)
                    bindingVDO.mIvSound.setImageResource(R.drawable.img_sound_enable)
                }
            }

            bindingVDO.mFlBanner.setOnClickListener {
                currentPosition = bindingVDO.mVideoView.currentPosition
                bindingVDO.mVideoView.pause()
                bindingVDO.mFlBanner.isEnabled = false
                if(pageBanner.Multilinkurlbrowser != ("")){
                    val intent =  Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                    mContext.startActivity(intent)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    bindingVDO.mFlBanner.isEnabled = true },1000)
            }

            bindingVDO.mFlDetail.setOnClickListener {
                currentPosition = bindingVDO.mVideoView.currentPosition
                bindingVDO.mVideoView.pause()
                bindingVDO.mFlDetail.isEnabled = false
                if(pageBanner.Multilinkurlbrowser != ("")){
                    val intent =  Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(pageBanner.Multilinkurlbrowser)
                    mContext.startActivity(intent)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    bindingVDO.mFlDetail.isEnabled = true },1000)
            }

            bindingVDO.mTvSkip.setOnClickListener {
                bindingVDO.mIvSound.setImageResource(R.drawable.img_sound_enable)
                mPopupListener.onClose()
                if(popupDialog != null){
                    popupDialog!!.dismiss()
                }
            }
            bindingVDO.mBtClosePopUp.setOnClickListener {
                bindingVDO.mIvSound.setImageResource(R.drawable.img_sound_enable)
                mPopupListener.onClose()
                if(popupDialog != null){
                    popupDialog!!.dismiss()
                }
            }
            bindingVDO.mTvAppName.text = pageBanner.Appbannername
            bindingVDO.mTvAppDesc.text = Config.adsCountry
            Glide.with(mContext).load(pageBanner.Appbannericonurl).into(bindingVDO.mIvAppIcon)
            Glide.with(mContext).load(pageBanner.Multilinkvdoposter).into(bindingVDO.mIvVdoPoster)
            bindingVDO.mVideoView.setZOrderOnTop(true)

            popupDialog = Dialog(mContext, R.style.PopupDialog)
            if (bindingVDO.root.parent != null){
                (bindingVDO.root.parent as ViewGroup).removeView(bindingVDO.root)
            }
            popupDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            popupDialog!!.setContentView(bindingVDO.root)
            popupDialog!!.setCancelable(true)
            popupDialog!!.setOnCancelListener { dialog ->
                if (popupDialog != null) {
                    dialog.cancel()
                    mPopupListener.onClose()
                }
            }
            bindingVDO.mVideoView.setOnPreparedListener {
                bindingVDO.mVideoView.seekTo(currentPosition)
                bindingVDO.mVideoView.start()
            }
            bindingVDO.mVideoView.visibility = View.VISIBLE
            bindingVDO.mVideoView.setVideoURI(Uri.parse(pageBanner.Multilink))
            bindingVDO.mVideoView.requestFocus()
            bindingVDO.mVideoView.start()

        }catch (e : Exception){
            e.printStackTrace()
            Const.log(tagS,"setVDO : catch")
            mPopupListener.onFailed("Load Failed")
        }
    }

    fun show() {
        try {
            if (popupDialog != null) {
                Const.log(tagS, "popupDialog : show")
                popupDialog!!.show()
                if (bPopupVdo && timeSkip != "") {
                    popupDialog!!.setOnKeyListener(object : DialogInterface.OnKeyListener {
                        override fun onKey(
                            dialog: DialogInterface,
                            keyCode: Int,
                            event: KeyEvent
                        ): Boolean {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                                dialog.cancel()
                                return true
                            }
                            return false
                        }
                    })
                    bPrepare = true
                    mTimerLoadVideo = object : CountDownTimer(timeSkip.toLong() * 1000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            bSkip = false
                        }

                        override fun onFinish() {
                            if (!bSkip) {
                                bindingVDO.mTvSkip.isEnabled = true
                                bindingVDO.mTvSkip.text = mContext.getString(R.string.skip)
                                bindingVDO.mCardSkip.visibility = View.VISIBLE
                            }
                        }
                    }.start()
                    bindingVDO.mVideoView.setOnCompletionListener {
                        bindingVDO.mBtClosePopUp.visibility = View.VISIBLE
                        bindingVDO.mIvVdoPoster.visibility = View.VISIBLE
                        bindingVDO.mCardSkip.visibility = View.GONE
                        bindingVDO.mVideoView.visibility = View.GONE
                        popupDialog!!.setOnKeyListener(object :DialogInterface.OnKeyListener {
                            override fun onKey(dialog : DialogInterface,keyCode : Int,event : KeyEvent) : Boolean {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    mPopupListener.onClose()
                                    dialog.dismiss()
                                    return false
                                }
                                return true
                            }
                        })
                    }
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
            mPopupListener.onFailed("Load Failed")
        }
    }
}