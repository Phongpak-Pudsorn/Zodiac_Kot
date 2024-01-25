package com.starvision.bannersdk

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.*
import android.webkit.*
import android.widget.BaseAdapter
import android.widget.FrameLayout
import com.google.gson.Gson
import com.starvision.AppPreferences
import com.starvision.Const
import com.starvision.api.LoadData
import com.starvision.bannersdk.databinding.ListviewNoticeMessageBinding
import com.starvision.bannersdk.databinding.NoticesMessageItemBinding
import com.starvision.bannersdk.databinding.PopupWebviewDialogBinding
import com.starvision.models.AdsModel
import java.text.SimpleDateFormat


class NoticeAdsListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val mContext = context
    private val loadData = LoadData(mContext)
    private val appPreferences = AppPreferences
    private val tagS = this.javaClass.simpleName
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("HH:mm:ss")
    private var noticeAds : FrameLayout? = null

    private val bindingListview : ListviewNoticeMessageBinding by lazy { ListviewNoticeMessageBinding.inflate(LayoutInflater.from(context)) }
    private val bindingNoticeItem : NoticesMessageItemBinding by lazy { NoticesMessageItemBinding.inflate(LayoutInflater.from(context)) }
    private val bindingWebview : PopupWebviewDialogBinding by lazy { PopupWebviewDialogBinding.inflate(LayoutInflater.from(context)) }

    private var mNoticeAdsListViewListener: NoticeAdsListViewListener? = null

    interface NoticeAdsListViewListener {
        fun onSuccess(strJson: String?)
        fun onNoticeClick(strJson: String?)
        fun onFailed(strErrorMessage: String?)
    }

    fun setNoticeAdsListener(listener: NoticeAdsListViewListener?) {
        mNoticeAdsListViewListener = listener
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
                mNoticeAdsListViewListener!!.onFailed("Notice Error")

            }else if (result > (10 * 60 * 1000)){
                Const.log(tagS,"loadAds : else if = เกิน10นาที โหลด API ใหม่")
                loadData.loadAdsData(true)
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
            mNoticeAdsListViewListener!!.onFailed("Notice Error")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setApiToView(dataApi : String, strUUID : String){
        Const.log(tagS,"strUUID : $strUUID")
        try {
            val adsModel = Gson().fromJson(dataApi, AdsModel::class.java)
            if(adsModel.Status == "True") {
                val dataRowPublish = adsModel.Datarowpublish
                noticeAds = this
                try {
                    Const.log(tagS,"try check Publishtitle : $dataRowPublish")
                    if (dataRowPublish[0].Publishtitle == ""){
                        Const.log(tagS,"dataRowPublish in null : $dataRowPublish")
                        mNoticeAdsListViewListener!!.onFailed("No Notice")
                    }else{
                        Const.log(tagS,"dataRowPublish else : $dataRowPublish")
                        (noticeAds as NoticeAdsListView).removeAllViews()
                        (noticeAds as NoticeAdsListView).addView(bindingListview.root)
                        bindingListview.mTvTextNoticeMessage.adapter = AdapterNotice(mContext,dataRowPublish)
                        mNoticeAdsListViewListener!!.onSuccess(dataRowPublish.toString())

                    }
                }catch (e : Exception){
                    Const.log(tagS,"Catch : setApiToView")
                    mNoticeAdsListViewListener!!.onFailed("No Notice")
                }
            }
        }catch (e : Exception){
            Const.log(tagS,"Catch : setApiToView")
            mNoticeAdsListViewListener!!.onFailed("Notice Error")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun showPopupWebForNoticeMessageBar(strUrl : String){
        val dialog = Dialog(mContext)
        dialog.setOnDismissListener { (bindingWebview.root.parent as ViewGroup).removeView(bindingWebview.root) }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingWebview.root)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.setCancelable(true)
        bindingWebview.mWebView.settings.javaScriptEnabled = true
        bindingWebview.mWebView.webChromeClient = WebChromeClient()
        bindingWebview.mWebView.webViewClient = WebViewClient()
        bindingWebview.mWebView.loadUrl(strUrl)
        bindingWebview.mWebView.settings.displayZoomControls = false
        bindingWebview.mWebView.settings.builtInZoomControls = true
        bindingWebview.mWebView.settings.setSupportZoom(true)
        bindingWebview.mWebView.settings.loadWithOverviewMode = true
        bindingWebview.mWebView.settings.useWideViewPort = true
        bindingWebview.mWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                bindingWebview.mPbLoad.visibility = View.GONE
            }
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                bindingWebview.mWebView.loadData(mContext.getString(R.string.text_no_net), "text/html", "UTF-8")
            }
        }
        bindingWebview.mBtGoHome.setOnClickListener {
            bindingWebview.mWebView.loadUrl(strUrl)
        }
        bindingWebview.mBtGoBack.setOnClickListener {
            bindingWebview.mWebView.goBack()
        }
        bindingWebview.mBtGoForword.setOnClickListener {
            bindingWebview.mWebView.goForward()
        }
        bindingWebview.mBtClosePopUp.setOnClickListener{
            bindingNoticeItem.mTvReadMore.isEnabled = true
            dialog.dismiss()
        }
        dialog.show()
    }

    inner class AdapterNotice(private val context: Context,private val dataJson : ArrayList<AdsModel.Datapublish>) : BaseAdapter() {
        override fun getCount(): Int {
            return dataJson.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            try {
                bindingNoticeItem.mTvNoticeMessage.text = dataJson[position].Publishtitle
                bindingNoticeItem.mTvDate.text = dataJson[position].modificationdate.substring(0,16)
                bindingNoticeItem.mTvReadMore.setOnClickListener {
                    bindingNoticeItem.mTvReadMore.isEnabled = false
                    if (dataJson[position].Publishtype == "LinkToWeb"){
                        Handler(Looper.getMainLooper()).postDelayed({
                            bindingNoticeItem.mTvReadMore.isEnabled = true
                        },1000)
                        Const.openPlayStore(mContext,dataJson[position].Publishdetail)
                    }else if(dataJson[position].Publishtype == "LinkToURL"){
                        showPopupWebForNoticeMessageBar(dataJson[position].Publishdetail)
                    }
                }
                bindingNoticeItem.mTvNoticeMessage.setOnClickListener {
                    bindingNoticeItem.mTvNoticeMessage.isEnabled = false
                    if (dataJson[position].Publishtype == "LinkToWeb"){
                        Handler(Looper.getMainLooper()).postDelayed({
                            bindingNoticeItem.mTvNoticeMessage.isEnabled = true
                        },1000)
                        Const.openPlayStore(mContext,dataJson[position].Publishdetail)
                    }else if(dataJson[position].Publishtype == "LinkToURL"){
                        showPopupWebForNoticeMessageBar(dataJson[position].Publishdetail)
                    }
                }
                bindingNoticeItem.mTvDate.setOnClickListener {
                    bindingNoticeItem.mTvDate.isEnabled = false
                    if (dataJson[position].Publishtype == "LinkToWeb"){
                        Handler(Looper.getMainLooper()).postDelayed({
                            bindingNoticeItem.mTvDate.isEnabled = true
                        },1000)
                        Const.openPlayStore(mContext,dataJson[position].Publishdetail)
                    }else if(dataJson[position].Publishtype == "LinkToURL"){
                        showPopupWebForNoticeMessageBar(dataJson[position].Publishdetail)
                    }
                }
            }catch (e :Exception){
                e.printStackTrace()
            }
            return bindingNoticeItem.root
        }
    }

}