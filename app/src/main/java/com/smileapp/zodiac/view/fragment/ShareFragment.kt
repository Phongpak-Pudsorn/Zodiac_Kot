package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentShareBinding

class ShareFragment:Fragment() {
    var bannerShow:BannerShow?=null
    val binding:FragmentShareBinding by lazy { FragmentShareBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
    }
}