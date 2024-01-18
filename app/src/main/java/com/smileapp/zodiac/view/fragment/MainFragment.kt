package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.api.Url
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.commonclass.MultiDirectionSlidingDrawer
import com.smileapp.zodiac.databinding.FragmentMainBinding
import com.smileapp.zodiac.utils.Utils
import com.starvision.bannersdk.NoticeAds

class MainFragment:Fragment() {
    var bannerShow:BannerShow?=null
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
        if (Utils.getOpenProfile()){
            binding.userTab.drawer.open()
            binding.userTab.handle.setImageResource(R.mipmap.ic_slide_open)
            binding.RlHead.visibility = View.VISIBLE
        }else{
            binding.userTab.drawer.close()
            binding.userTab.handle.setImageResource(R.mipmap.ic_slide_close)
            binding.RlHead.visibility = View.GONE
        }
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
        binding.userTab.TvRasi.text = Utils.getNameAndDateRasi()
        binding.btnMonth.text = Utils.getNameMonth()
        binding.btnYear.text = Utils.getNameYear()
        Utils.setTextGradient_Blue(binding.userTab.TvNameUser)
        Utils.setTextGradient_Green(binding.userTab.TvRasi)
        if (Utils.getGENDER() =="Man"){
            binding.userTab.ImgRasi.setImageResource(R.mipmap.img_sex_man_trans)
        }else if (Utils.getGENDER() =="Woman"){
            binding.userTab.ImgRasi.setImageResource(R.mipmap.img_sex_woman_trans)
        }
        binding.userTab.drawer.setOnDrawerOpenListener(object : MultiDirectionSlidingDrawer.OnDrawerOpenListener{
            override fun onDrawerOpened() {
                Utils.setOpenProfile(true)
                binding.RlHead.visibility = View.VISIBLE
                binding.userTab.handle.setImageResource(R.mipmap.ic_slide_open)
            }
        })
        binding.userTab.drawer.setOnDrawerCloseListener(object :MultiDirectionSlidingDrawer.OnDrawerCloseListener{
            override fun onDrawerClosed() {
                Utils.setOpenProfile(false)
                binding.RlHead.visibility = View.GONE
                binding.userTab.handle.setImageResource(R.mipmap.ic_slide_close)
            }
        })
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
            Utils.setWebTitle(getString(R.string.zodiac_week))
            Utils.setWebUrl(Url.weekUrl)
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
        }
        binding.btnMonth.setOnClickListener {
            Utils.setWebTitle(getString(R.string.zodiac_month))
            Utils.setWebUrl(Url.monthUrl)
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
        }
        binding.btnYear.setOnClickListener {
            Utils.setWebTitle(getString(R.string.zodiac_year))
            Utils.setWebUrl(Url.yearUrl)
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

    override fun onStart() {
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
        super.onStart()
    }

    override fun onResume() {
        bannerShow!!.getShowBannerSmall(0)
        super.onResume()
    }
}