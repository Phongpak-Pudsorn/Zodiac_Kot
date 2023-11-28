package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.databinding.FragmentPredictBinding
import com.smileapp.zodiac.utils.Utils

class PredictFragment:Fragment() {
    var bannerShow:BannerShow?=null
    val binding:FragmentPredictBinding by lazy { FragmentPredictBinding.inflate(layoutInflater) }
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
    }
}