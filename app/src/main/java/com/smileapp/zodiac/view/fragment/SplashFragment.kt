package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.CallVersion
import com.smileapp.zodiac.databinding.FragmentSplashBinding
import com.smileapp.zodiac.utils.Utils
import com.starvision.AppPreferences
import com.starvision.ConstVisionInstallSDK
import com.starvision.ccusdk.StarVisionCcuSDK
import com.starvision.installsdk.StarVisionInstallSDK

class SplashFragment:Fragment() {
    var bannerShow: BannerShow?=null
    val binding:FragmentSplashBinding by lazy { FragmentSplashBinding.inflate(layoutInflater) }
    var appPreferences : AppPreferences?=null
    var firstInit = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appPreferences = AppPreferences
        appPreferences?.setPreferences(requireActivity(), AppPreferences.KEY_CHECK_LOAD_ADS_API,true)
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
        bannerShow!!.loadPopupBanner(1)
        val version = CallVersion(requireActivity())
        Utils.setNoticeAds(false)
        setStarVisionSDK()
        version.setCallListener(object :CallVersion.CallVersionListener{
            override fun onSuccess() {
                initial()
            }

            override fun onGotUpdate() {
            }

            override fun onFailed() {
                requireActivity().finish()
            }
        })

    }
    fun initial(){
        if(Utils.getPrefer(requireActivity(), Utils.KEY_ACCEPT, false)==false) {
            val policy = PolicyDialogFragment.newInstance()
            policy.show(childFragmentManager, "PolicyDialogFragment")
            policy.setOnClickAdapterListener(object : PolicyDialogFragment.onClickAdapterListener {
                override fun onClick(i: Boolean) {
                    Utils.setPrefer(requireActivity(),Utils.KEY_ACCEPT,i)
                    timer.start()
                }
            })
        }else{
            timer.start()
        }
    }
    fun setStarVisionSDK(){
        val starVisionCcuSDK = StarVisionCcuSDK(requireActivity())
        val dot = "."
        val arr = requireActivity().packageName.split(dot).toTypedArray()
        starVisionCcuSDK.startService("8888", ConstVisionInstallSDK.getUUID(requireActivity())!!, requireActivity().packageName, arr[2])

        val callWebServerSendContact = StarVisionInstallSDK(requireActivity())
        callWebServerSendContact.setUrlInstall("https://starvision.in.th/appbannersdk/serverweb/sdk_app_install.php");
        callWebServerSendContact.setPacketNameInstall(requireActivity().packageName)
        callWebServerSendContact.setAppBannerID("155")
        callWebServerSendContact.setSendContact(false)
        callWebServerSendContact.setGetAccount(false)
        callWebServerSendContact.startService()
    }
    val timer = object : CountDownTimer(5000, 1000) {
        override fun onTick(p0: Long) {
            Log.e("time", p0.toString())
        }
        override fun onFinish() {
            bannerShow!!.showPopupBannerNow(1,object : BannerShow.onAdClosed{
                override fun onAdClosed() {
                    if (Utils.getPrefer(requireActivity(),Utils.KEY_FIRST_OPEN,false)==false){
                        Utils.setPrefer(requireActivity(),Utils.KEY_FIRST_OPEN,true)
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_splashFragment_to_profileFragment)
                    }else {
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_splashFragment_to_mainFragment)
                    }
                }
            })
        }

    }

    override fun onPause() {
        firstInit = true
        if(Utils.getPrefer(requireActivity(), Utils.KEY_ACCEPT, false)==true){
            timer.cancel()
        }
        super.onPause()
    }

    override fun onResume() {
        if (firstInit) {
            if (Utils.getPrefer(requireActivity(), Utils.KEY_ACCEPT, false) == true) {
                timer.start()
            }
        }
        super.onResume()
    }
}