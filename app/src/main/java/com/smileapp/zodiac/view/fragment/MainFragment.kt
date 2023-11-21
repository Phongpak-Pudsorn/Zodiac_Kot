package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentMainBinding
import com.smileapp.zodiac.utils.Utils
import com.starvision.bannersdk.NoticeAds

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
        setNoticeAds()
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnZodiac12,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnToDay,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnWeek,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnMonth,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnYear,32)
        Font().styleText_RSU_BOLD(requireActivity(),binding.btnRecommend,32)
        Font().styleText_RSU_Reg(requireActivity(),binding.userTab.TvRasi,24)
        Font().styleText_RSU_Reg(requireActivity(),binding.userTab.TvNameUser,34)
        binding.userTab.TvNameUser.text = Utils.getNameUser()
        binding.userTab.TvRasi.text =Utils.getNameAndDateRasi()
        Utils.setTextGradient_Blue(binding.userTab.TvNameUser)
        Utils.setTextGradient_Green(binding.userTab.TvRasi)
        if (Utils.getGENDER() =="Man"){
            binding.userTab.ImgRasi.setImageResource(R.mipmap.img_sex_man_trans)
        }else if (Utils.getGENDER() =="Woman"){
            binding.userTab.ImgRasi.setImageResource(R.mipmap.img_sex_woman_trans)
        }
        binding.imgSetting.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_settingFragment)
        }
        binding.userTab.mImguser.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_editProfileFragment)
        }
        binding.btnZodiac12.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_menuZodiacFragment)
        }
        binding.btnToDay.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_zodiacTodayFragment)
        }
        binding.btnWeek.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
        }
        binding.btnMonth.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
        }
        binding.btnYear.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
        }
        binding.btnRecommend.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_zodiacRecommend)
        }
    }
    fun setNoticeAds(){
        binding.noticeAds.setNoticeAdsListener(object : NoticeAds.NoticeAdsListener{
            override fun onSuccess(strJson: String?) {
                Log.e("noticeAds", "noticeAds onSuccessListener: strJson "+strJson)
                if (!Utils.getNoticeAds()) {
                    binding.noticeAds.visibility = View.VISIBLE
                }else{
                    binding.noticeAds.visibility = View.GONE
                }
            }

            override fun onBannerClick(strJson: String?) {
                Log.e("noticeAds", "noticeAds onBannerClick: strJson "+strJson)
            }

            override fun onFailed(strErrorMessage: String?) {
                Log.e("noticeAds", "noticeAds onFailedListener: strErrorMessage "+strErrorMessage)
                binding.noticeAds.visibility = View.GONE
            }

            override fun onClose() {
                Log.e("noticeAds", "noticeAds onClose")
                Utils.setNoticeAds(true)
                binding.noticeAds.visibility = View.GONE
            }
        })
        binding.noticeAds.loadAds(Utils.UUID)
    }
}