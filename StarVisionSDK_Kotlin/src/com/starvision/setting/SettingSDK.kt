package com.starvision.setting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.starvision.Const
import com.starvision.ConstVisionInstallSDK
import com.starvision.bannersdk.NoticeAdsListView
import com.starvision.bannersdk.R
import com.starvision.bannersdk.databinding.PopupWebDialogBinding
import com.starvision.bannersdk.databinding.SettingAboutBinding


class SettingSDK(context : Context) {

    private val bindingSetting : SettingAboutBinding by lazy { SettingAboutBinding.inflate(LayoutInflater.from(context))}
    private val bindingWeb : PopupWebDialogBinding by lazy { PopupWebDialogBinding.inflate(LayoutInflater.from(context))}

    private val mContext = context

    fun setVerSion(strVersion: String): String {
        return mContext.getString(R.string.text_ver) + " " + strVersion
    }

    fun setAbout(strText : String){
        val dialog =  Dialog(mContext)
        dialog.setOnDismissListener { (bindingSetting.root.parent as ViewGroup).removeView(bindingSetting.root) }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingSetting.root)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.setCancelable(true)
        bindingSetting.layoutPublic.removeAllViews()
        bindingSetting.layoutPublic.addView(bindingSetting.mTvText)
        bindingSetting.mTvText.visibility = View.VISIBLE
        bindingSetting.mTvTitleSetting.visibility = View.VISIBLE
        bindingSetting.mTvTitleSetting.text = mContext.getText(R.string.text_about_setting)
        bindingSetting.mTvText.text = strText
        bindingSetting.mBtClosePopUp.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setPolicy(){
        val dialog = Dialog(mContext)
        dialog.setOnDismissListener { (bindingSetting.root.parent as ViewGroup).removeView(bindingSetting.root) }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingSetting.root)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.setCancelable(true)
        bindingSetting.layoutPublic.removeAllViews()
        bindingSetting.layoutPublic.addView(bindingWeb.root)
        bindingSetting.mTvTitleSetting.text = mContext.getText(R.string.text_policy)
        bindingSetting.mTvTitleSetting.visibility = View.VISIBLE
        bindingWeb.mFlBackground.visibility = View.GONE
        bindingWeb.mWebView.settings.javaScriptEnabled = true
        bindingWeb.mWebView.webChromeClient = WebChromeClient()
        bindingWeb.mWebView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                bindingWeb.mPbLoad.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
        bindingWeb.mWebView.loadUrl(mContext.getString(R.string.url_policy))
        bindingSetting.mBtClosePopUp.setOnClickListener{
            bindingWeb.mFlBackground.visibility = View.VISIBLE
            dialog.dismiss()
        }
        dialog.show()
    }

    fun setPublic(){
        val dialog = Dialog(mContext)
        dialog.setOnDismissListener { (bindingSetting.root.parent as ViewGroup).removeView(bindingSetting.root) }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingSetting.root)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(R.color.black)
        dialog.setCancelable(true)
        bindingSetting.layoutPublic.removeAllViews()
        bindingSetting.mTvText.visibility = View.GONE
        bindingSetting.mTvTitleSetting.visibility = View.VISIBLE
        bindingSetting.mTvTitleSetting.text = mContext.getText(R.string.text_public)
        val noticeAdsListView = NoticeAdsListView(mContext)
        noticeAdsListView.setNoticeAdsListener(object :
            NoticeAdsListView.NoticeAdsListViewListener {
            override fun onSuccess(strJson: String?) {
                bindingSetting.mTvText.visibility = View.GONE
            }

            override fun onNoticeClick(strJson : String?) {

            }

            override fun onFailed(strErrorMessage : String?) {
                bindingSetting.layoutPublic.removeAllViews()
                bindingSetting.layoutPublic.addView(bindingSetting.mTvText)
                bindingSetting.mTvText.visibility = View.VISIBLE
                bindingSetting.mTvText.text = mContext.getText(R.string.text_public_none)

            }
        })
        noticeAdsListView.loadAds(ConstVisionInstallSDK.getUUID(mContext)!!)
        bindingSetting.layoutPublic.addView(noticeAdsListView)

        bindingSetting.mBtClosePopUp.setOnClickListener {
            bindingSetting.layoutPublic.removeAllViews()
            bindingSetting.mTvText.visibility = View.VISIBLE
            dialog.dismiss()
        }
        dialog.show()
    }

    fun setShare(strShare : String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, strShare)
        mContext.startActivity(Intent.createChooser(share, "Share App"))
        Toast.makeText(mContext, "Share App...", Toast.LENGTH_SHORT).show()
    }

    fun setRating(){
        try {
            if(Const.appInstalledOrNot(mContext, "com.android.vending")){
                val sendIntent = Intent(Intent.ACTION_VIEW)
                sendIntent.data = Uri.parse("market://details?id="+mContext.packageName)
                sendIntent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity")
                mContext.startActivity(sendIntent)
                Const.log("TAG","setRating try if")
            } else {
                Const.log("TAG","setRating try else")
                mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+mContext.packageName)))
            }
        } catch (anfe :android.content.ActivityNotFoundException) {
            Const.log("TAG","setRating catch ")
            mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+mContext.packageName)))
            anfe.printStackTrace()
        } catch( e : Exception) {
            e.printStackTrace()
        }
    }

    fun setAppMe(strDev : String){
        try {
            if(Const.appInstalledOrNot(mContext, "com.android.vending")){
                Const.log("TAG","setAppMe try if")
                val sendIntent = Intent(Intent.ACTION_VIEW)
                sendIntent.data = Uri.parse("market://search?q=pub:$strDev")
                sendIntent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity")
                mContext.startActivity(sendIntent)
            } else {
                Const.log("TAG","setAppMe try else")
                mContext.startActivity( Intent(Intent.ACTION_VIEW, Uri.parse(strDev)))
            }
        } catch (anfe : android.content.ActivityNotFoundException) {
            Const.log("TAG","setAppMe catch")
            mContext.startActivity( Intent(Intent.ACTION_VIEW, Uri.parse(strDev)))
            anfe.printStackTrace()
        } catch(e : Exception) {
            e.printStackTrace()
        }
    }

}