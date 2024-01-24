package com.smileapp.zodiac.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
//import kotlinx.android.synthetic.main.dialog_policy.*
//import kotlinx.android.synthetic.main.dialog_policy.view.*
import kotlin.system.exitProcess


class PolicyDialogFragment : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): PolicyDialogFragment {
            val args = Bundle()
            val fragment = PolicyDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url_web = resources.getString(R.string.web_policy)
        val web_view = view.findViewById<WebView>(R.id.web_view)
        val btn_ok = view.findViewById<AppCompatButton>(R.id.btn_ok)
        val btn_cancel = view.findViewById<AppCompatButton>(R.id.btn_cancel)
        url_web?.let { web_view.loadUrl(it) }
        web_view.webViewClient = object :WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
//                Log.e("onPageFinished", "onPageFinished")
            }
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError,
            ) {
                super.onReceivedError(view, request, error)
//                Log.e("onReceivedError error",error.toString())
//                Log.e("onReceivedError request",request.toString())
                val builder = AlertDialog.Builder(requireActivity())
                val alertDialog = builder.create()
                var message = "Certificate error. ErrorCode is "+error.errorCode.toString() +" "+ error.description.toString()
                when (error.errorCode) {
                    SslError.SSL_UNTRUSTED -> message =
                        "The certificate authority is not trusted."
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
//                    Log.d("CHECK", "Button ok pressed")
                    // Ignore SSL certificate errors
                    //                        handler.proceed();
                }
                alertDialog.setButton(
                    DialogInterface.BUTTON_NEGATIVE, "Cancel"
                ) { dialog, which ->
//                    Log.d("CHECK", "Button cancel pressed")
                    Navigation.findNavController(requireView()).navigateUp()
                    //                        handler.cancel();
                }
                if (error.errorCode>=0){
                    alertDialog.show()
                }else{
                    web_view.loadData("<div>Please check your internet connection.</div>",
                        "text/html", "UTF-8")
                }
            }

        }

        val webSettings: WebSettings = web_view.getSettings()
        webSettings.javaScriptEnabled = true
//        view.btn_ok.setOnClickListener {
//            diologListener?.onClick(true)
//            dismiss()
//        }
        btn_ok.setOnClickListener {
            diologListener?.onClick(true)
            dismiss()
        }
//        view.btn_cancel.setOnClickListener { v ->
//            dismiss()
//            exitProcess(0);
//        }
        btn_cancel.setOnClickListener { v ->
            dismiss()
            exitProcess(0);
        }

    }

    var diologListener: onClickAdapterListener? = null
    interface onClickAdapterListener {
        fun onClick(i: Boolean)
    }
    fun setOnClickAdapterListener(listener: onClickAdapterListener) {
        diologListener = listener
    }

}