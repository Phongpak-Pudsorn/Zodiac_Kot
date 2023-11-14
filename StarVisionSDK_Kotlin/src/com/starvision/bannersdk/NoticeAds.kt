package com.starvision.bannersdk

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.google.gson.Gson
import com.starvision.AppPreferences
import com.starvision.Const
import com.starvision.api.LoadData
import com.starvision.bannersdk.databinding.PopupWebviewDialogBinding
import com.starvision.bannersdk.databinding.TextviewNoticeMessageBinding
import com.starvision.models.AdsModel
import java.text.SimpleDateFormat

class NoticeAds @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val mContext = context
    private val loadData = LoadData(mContext)
    private val appPreferences = AppPreferences
    private val tagS = this.javaClass.simpleName
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("HH:mm:ss")
    private var noticeAds : LinearLayout? = null

    private val bindingTextview : TextviewNoticeMessageBinding by lazy { TextviewNoticeMessageBinding.inflate(LayoutInflater.from(context)) }
    private val bindingWebView : PopupWebviewDialogBinding by lazy { PopupWebviewDialogBinding.inflate(LayoutInflater.from(context)) }

    private var mNoticeAdsListener: NoticeAdsListener? = null

    interface NoticeAdsListener {
        fun onSuccess(strJson: String?)
        fun onBannerClick(strJson: String?)
        fun onFailed(strErrorMessage: String?)
        fun onClose()
    }

    fun setNoticeAdsListener(listener: NoticeAdsListener?) {
        mNoticeAdsListener = listener
    }

    fun loadAds (strUUID : String){
        Const.log(tagS,"loadAds")
        try {
            Const.log(tagS,"try")
            val timeNow = dateFormat.format(System.currentTimeMillis())
            val timeData = dateFormat.parse((appPreferences.getPreferences(mContext,AppPreferences.KEY_PREFS_TIME_LOADS,"")).toString())
            val result : Long =  (dateFormat.parse(timeNow)!!.time - timeData!!.time)
            if(loadData.checkData == false){
                Const.log(tagS,"loadAds : if = โหลด Api ไม่ได้")
                mNoticeAdsListener!!.onFailed("Notice Error")

            }else if (result > (10 * 60 * 1000)){
                Const.log(tagS,"loadAds : else if = เกิน10นาที โหลด API ใหม่")
                loadData.loadAdsData()
                val newdataApi = appPreferences.getPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA,"")
                setApiToView(newdataApi.toString(),strUUID)

            }else{
                Const.log(tagS,"loadAds : else = ยังไม่เกิน10นาที ใช้ Json Preferences")
                val dataApi = appPreferences.getPreferences(mContext, AppPreferences.KEY_PREFS_JSON_DATA,"")
                setApiToView(dataApi.toString(),strUUID)
            }
        }catch (e : Exception){
            Const.log(tagS,"catch")
            Const.log(tagS,"loadAds : catch")
            mNoticeAdsListener!!.onFailed("Notice Error")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setApiToView(dataApi : String, strUUID : String){
        Const.log(tagS,"strUUID : $strUUID")
        try {
            val adsModel = Gson().fromJson(dataApi, AdsModel::class.java)
            if(adsModel.Status == "True") {
                val datarowPublish = adsModel.Datarowpublish
                for (i in datarowPublish.indices) {
                    noticeAds = this
                    (noticeAds as NoticeAds).addView(bindingTextview.root)
                    bindingTextview.mTvTextNoticeMessage.text = datarowPublish[i].Publishtitle
                    bindingTextview.mTvTextNoticeMessage.isSelected = true
                    Const.log(tagS,"Text :"+datarowPublish[i].Publishtitle)
                    bindingTextview.mBtCloseNoticeMessage.setOnClickListener {
                        bindingTextview.root.isEnabled = true
                        mNoticeAdsListener!!.onClose()
                    }

                    bindingTextview.root.setOnClickListener {
                        bindingTextview.root.isEnabled = false
                        Const.log(tagS,"Publishtype : "+datarowPublish[i].Publishtype)
                        if (datarowPublish[i].Publishtype == "LinkToAPP"){
                            Const.openPlayStore(mContext,datarowPublish[i].Publishdetail)
                        }else if(datarowPublish[i].Publishtype == "LinkToURL"){
                            showPopupWebForNoticeMessageBar(datarowPublish[i].Publishdetail)
                        }
                    }

                }
                mNoticeAdsListener!!.onSuccess(datarowPublish.toString())
            }
        }catch (e : Exception){
            mNoticeAdsListener!!.onFailed("Notice Error")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun showPopupWebForNoticeMessageBar(strUrl : String){
        val dialog = Dialog(mContext)
        dialog.setOnDismissListener { (bindingWebView.root.parent as ViewGroup).removeView(bindingWebView.root) }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingWebView.root)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.setCancelable(true)
        bindingWebView.mWebView.settings.javaScriptEnabled = true
        bindingWebView.mWebView.webChromeClient = WebChromeClient()
        bindingWebView.mWebView.webViewClient = WebViewClient()
        bindingWebView.mWebView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                bindingWebView.mPbLoad.visibility = View.GONE
                bindingWebView.mBtGoForword.isEnabled = bindingWebView.mWebView.canGoForward()
                bindingWebView.mBtGoBack.isEnabled = bindingWebView.mWebView.canGoBack()
                super.onPageFinished(view, url)
            }
    }
        bindingWebView.mWebView.loadUrl(strUrl)
        bindingWebView.mWebView.settings.displayZoomControls = false
        bindingWebView.mWebView.settings.builtInZoomControls = true
        bindingWebView.mWebView.settings.setSupportZoom(true)
        bindingWebView.mWebView.settings.loadWithOverviewMode = true
        bindingWebView.mWebView.settings.useWideViewPort = true

        bindingWebView.mBtGoHome.setOnClickListener {
            bindingWebView.mWebView.loadUrl(strUrl)
        }
        bindingWebView.mBtGoBack.setOnClickListener {
            bindingWebView.mWebView.goBack()
        }
        bindingWebView.mBtGoForword.setOnClickListener {
            bindingWebView.mWebView.goForward()
        }
        bindingWebView.mBtClosePopUp.setOnClickListener{
            bindingTextview.root.isEnabled = true
            dialog.dismiss()
        }
        dialog.show()
    }
}