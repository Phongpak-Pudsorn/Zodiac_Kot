package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentMainBinding
import com.smileapp.zodiac.utils.Utils

class MainFragment:Fragment() {
    val binding:FragmentMainBinding by lazy { FragmentMainBinding.inflate(layoutInflater) }
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
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnZodiac12,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnToDay,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnWeek,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnMonth,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnYear,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnRecommend,32)
        Font().styleText_RSU_Reg(requireActivity(),binding.userTab.TvRasi,24)
        Font().styleText_RSU_Reg(requireActivity(),binding.userTab.TvNameUser,34)
        binding.imgSetting.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_settingFragment)
        }
    }
}