package com.smileapp.zodiac.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentShareTodayBinding
import com.smileapp.zodiac.utils.Utils

class ShareTodayFragment:Fragment() {
    var bannerShow:BannerShow?=null
    val binding:FragmentShareTodayBinding by lazy { FragmentShareTodayBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
        bannerShow!!.getShowBannerSmall(10)
        binding.TvDay.text = Utils.getSharedDay()
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvDay,22)
        Utils.setStringImageDrawable(requireActivity(), binding.imgDay, Utils.getSharedImage(), 300, 215)
        binding.mWvDescription.setBackgroundColor(Color.TRANSPARENT)
        binding.mWvDescription.loadDataWithBaseURL(null,
            Utils.getHtmlData_While(requireActivity(), Utils.getSharedDesc()+"" +"<div  align=\"right\"><font  size=\"5\" color=\"red\">"+getString(
                R.string.text_share_nameapp_android)+"</font><div >", 26)!!, null, "utf-8", null)
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }
    }
}