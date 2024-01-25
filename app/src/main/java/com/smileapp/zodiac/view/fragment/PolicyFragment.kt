package com.smileapp.zodiac.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smileapp.zodiac.R
import com.smileapp.zodiac.databinding.FragmentPolicyBinding

class PolicyFragment:BottomSheetDialogFragment() {
    val binding: FragmentPolicyBinding by lazy { FragmentPolicyBinding.inflate(layoutInflater) }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { p0 ->
            val bottomSheetDialog = p0 as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }
    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = bottomSheet.height
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webPolicy.webViewClient = WebViewClient()
        binding.webPolicy.loadUrl(resources.getString(R.string.web_policy))
        binding.imgPolicyClose.setOnClickListener {
            dismiss()
        }
    }
    companion object{
        fun newInstance(): PolicyFragment {
            val fragment = PolicyFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }


    }

}