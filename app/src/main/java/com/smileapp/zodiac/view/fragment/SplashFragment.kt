package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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

class SplashFragment:Fragment() {
    var bannerShow:BannerShow?=null
    val binding:FragmentSplashBinding by lazy { FragmentSplashBinding.inflate(layoutInflater) }
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
        bannerShow!!.loadPopupBanner(1)
        val version = CallVersion(requireActivity())
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
        val accept = Utils.getPrefer(requireActivity(), Utils.KEY_ACCEPT, false)
        val firstOpen = Utils.getPrefer(requireActivity(),Utils.KEY_FIRST_OPEN,false)
        if (accept==false){
            val policy = PolicyDialogFragment.newInstance()
            policy.show(childFragmentManager,"SpinDialogFragment")
            policy.setOnClickAdapterListener(object :PolicyDialogFragment.onClickAdapterListener{
                override fun onClick(i: Boolean) {
                    Utils.setPrefer(requireActivity(),Utils.KEY_ACCEPT,i)
                    if (firstOpen==false){
                        Log.e("accept and first", "$accept $firstOpen")
                        val timer = object : CountDownTimer(3000, 1000) {
                            override fun onTick(p0: Long) {
                                Log.e("time", p0.toString())
                            }
                            override fun onFinish() {
                                bannerShow!!.showPopupBannerNow(1,object : BannerShow.onAdClosed{
                                    override fun onAdClosed() {
                                        Utils.setPrefer(requireActivity(),Utils.KEY_FIRST_OPEN,true)
                                        Navigation.findNavController(requireView())
                                            .navigate(R.id.action_splashFragment_to_profileFragment)
                                    }
                                })
                            }
                        }
                        timer.start()
                    }else{
                        Log.e("accept and first", "$accept $firstOpen")
                        val timer = object : CountDownTimer(3000, 1000) {
                            override fun onTick(p0: Long) {
                                Log.e("time", p0.toString())
                            }
                            override fun onFinish() {
                                bannerShow!!.showPopupBannerNow(1,object : BannerShow.onAdClosed{
                                    override fun onAdClosed() {
                                        Navigation.findNavController(requireView())
                                            .navigate(R.id.action_splashFragment_to_mainFragment)
                                    }
                                })
                            }

                        }
                        timer.start()
                    }
                }
            })

        }else{
            if (firstOpen==false){
                Log.e("accept and first", "$accept $firstOpen")
                val timer = object : CountDownTimer(3000, 1000) {
                    override fun onTick(p0: Long) {
                        Log.e("time", p0.toString())
                    }
                    override fun onFinish() {
                        bannerShow!!.showPopupBannerNow(1,object : BannerShow.onAdClosed{
                            override fun onAdClosed() {
                                Utils.setPrefer(requireActivity(),Utils.KEY_FIRST_OPEN,true)
                                Navigation.findNavController(requireView())
                                    .navigate(R.id.action_splashFragment_to_profileFragment)
                            }
                        })
                    }
                }
                timer.start()
            }else{
                Log.e("accept and first", "$accept $firstOpen")
                val timer = object : CountDownTimer(3000, 1000) {
                    override fun onTick(p0: Long) {
                        Log.e("time", p0.toString())
                    }
                    override fun onFinish() {
                        bannerShow!!.showPopupBannerNow(1,object : BannerShow.onAdClosed{
                            override fun onAdClosed() {
                                Navigation.findNavController(requireView())
                                    .navigate(R.id.action_splashFragment_to_mainFragment)
                            }
                        })
                    }

                }
                timer.start()
            }
        }
    }
}