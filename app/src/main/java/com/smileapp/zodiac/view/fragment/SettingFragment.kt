package com.smileapp.zodiac.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.BuildConfig
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.ChkInternet
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentSettingBinding
import com.smileapp.zodiac.utils.Utils
import com.starvision.setting.SettingSDK

class SettingFragment:Fragment() {
    var back = true
    var bannerShow: BannerShow?=null
    val binding:FragmentSettingBinding by lazy { FragmentSettingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
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
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        binding.Details.visibility = View.GONE
        binding.mTvVersion.text = getString(R.string.version)+" "+BuildConfig.VERSION_NAME
        binding.imgBack.setOnClickListener {
            if (back){
                back = false
                Navigation.findNavController(requireView()).navigateUp()
            }
        }
        if (Utils.getStatusNotic()) {
            binding.IvSwitchPus.setImageResource(R.mipmap.switch_on)
        } else {
            binding.IvSwitchPus.setImageResource(R.mipmap.switch_off)
        }
        binding.btnAbout.setOnClickListener {
            if (binding.Details.visibility == View.VISIBLE) {
                binding.Details.visibility = View.GONE
            } else {
                binding.Details.visibility = View.VISIBLE
            }

        }
        binding.IvSwitchPus.setOnClickListener {
            if (Utils.getStatusNotic()){
                Utils.setStatusNotic(false)
                binding.IvSwitchPus.setImageResource(R.mipmap.switch_off)
            }else{
                Utils.setStatusNotic(true)
                binding.IvSwitchPus.setImageResource(R.mipmap.switch_on)
            }
//            Log.e("setStatusNotic",Utils.getStatusNotic().toString())
        }
        binding.btnNotic.setOnClickListener {
            SettingSDK(requireContext()).setPublic()
//            Navigation.findNavController(requireView()).navigate(R.id.action_settingFragment_to_publicFragment)
        }
        binding.btnAppRate.setOnClickListener {
            val link = "https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID
            val intent = Intent(Intent.ACTION_VIEW)
            intent.putExtra(Intent.EXTRA_PACKAGE_NAME,"google")
            intent.data = Uri.parse(link)
            startActivity(intent)
        }
        binding.btnAppStarvision.setOnClickListener {
            if (ChkInternet(requireActivity()).isOnline) {
                val link = "https://play.google.com/store/apps/developer?id=Gostar+Company+Limited"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.putExtra(Intent.EXTRA_PACKAGE_NAME, "google")
                intent.data = Uri.parse(link)
                startActivity(intent)
            }else{
                Toast.makeText(requireActivity(),getString(R.string.text_nonet_thai),Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnPolicy.setOnClickListener {
            val dialog = PolicyFragment()
            dialog.show(childFragmentManager, "fragment_policy")
        }
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            @SuppressLint("SuspiciousIndentation")
            override fun handleOnBackPressed() {
                if (back){
                    back = false
                    Navigation.findNavController(requireView()).navigateUp()
                }
            }
        })
    }

    override fun onResume() {
        if (!Utils.showBanner) {
            Utils.showBanner = true
            bannerShow!!.getShowBannerSmall(10)
        }
        super.onResume()
    }
}