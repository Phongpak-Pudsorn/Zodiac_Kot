package com.smileapp.zodiac.view.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.WebviewBinding
import com.smileapp.zodiac.utils.Utils

class ZodiacWebFragment: Fragment() {
    val binding: WebviewBinding by lazy { WebviewBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        startWebView(Utils.getWebUrl().toString())
        binding.TvTitle.text = Utils.getWebTitle()
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigateUp()
        }
        binding.imgShare.setOnClickListener {
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_TEXT, strMessage + strUrl + " " + getString(R.string.text_share_hashtag))
//            Toast.makeText(requireActivity(), "Share..", Toast.LENGTH_SHORT).show()
//            startActivity(intent)
        }
    }
    private fun startWebView(url: String) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link
        binding.webview1.setWebViewClient(object : WebViewClient() {
            var progressDialog: ProgressDialog? = null

            //If you will not use this method url links are opeen in new brower not in webview
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            //Show loader on url load
            override fun onLoadResource(view: WebView, url: String) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = ProgressDialog(requireActivity())
                    progressDialog!!.setMessage("Loading...")
                    progressDialog!!.show()
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                try {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                    progressDialog = null;
//                }
                    progressDialog!!.dismiss()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError,
            ) {
                Log.e("TAG", "onReceivedSslError")
                val builder = AlertDialog.Builder(requireActivity())
                val alertDialog = builder.create()
                var message = "Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += " Do you want to continue anyway?"
                alertDialog.setTitle("SSL Certificate Error")
                alertDialog.setMessage(message)
                alertDialog.setButton(
                    DialogInterface.BUTTON_POSITIVE, "OK"
                ) { dialog, which ->
                    Log.d("CHECK", "Button ok pressed")
                    // Ignore SSL certificate errors
                    handler.proceed()
                }
                alertDialog.setButton(
                    DialogInterface.BUTTON_NEGATIVE, "Cancel"
                ) { dialog, which ->
                    Log.d("CHECK", "Button cancel pressed")
                    handler.cancel()
                }
                alertDialog.show()
            }
        })

        // Javascript inabled on webview
        binding.webview1.getSettings().setJavaScriptEnabled(true)

        // Other webview options
        // webView.getSettings().setLoadWithOverviewMode(true);
        // webView.getSettings().setUseWideViewPort(true);
        // webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        binding.webview1.setScrollbarFadingEnabled(false)
        binding.webview1.getSettings().setBuiltInZoomControls(true)
        binding.webview1.getSettings().setJavaScriptEnabled(true)
        binding.webview1.getSettings().setAllowFileAccess(true)

        //Load url in webview
        binding.webview1.loadUrl(url)
    }

}