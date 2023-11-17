package com.smileapp.zodiac.view.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
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

        web_view.setWebViewClient(WebViewClient())
        url_web?.let { web_view.loadUrl(it) }

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