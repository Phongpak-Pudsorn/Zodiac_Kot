package com.smileapp.zodiac.view.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.api.Url
import com.smileapp.zodiac.commonclass.*
import com.smileapp.zodiac.databinding.FragmentMainBinding
import com.smileapp.zodiac.utils.Utils
import com.starvision.bannersdk.NoticeAds
import java.io.File

class MainFragment:Fragment() {
    // Declare the launcher at the top of your Activity/Fragment:
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
//            Log.e("requestPermissionLauncher","if")
            // FCM SDK (and your app) can post notifications.
        } else {
//            Log.e("requestPermissionLauncher","else")
            // TODO: Inform user that that your app will not show notifications.
        }
    }
    val imgUserFile = "user_photo.jpg"
    var backAble = true
    var bannerShow:BannerShow?=null
    val handler = Handler(Looper.getMainLooper())
    val binding:FragmentMainBinding by lazy { FragmentMainBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var progressDialog = LoadingDialog.progressDialog(requireActivity())
        progressDialog.show()
        setNoticeAds()
        setObject()
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (backAble) {
                        backAble = false
                        alertDialogExit()
                    }
                }
            })
        handler.postDelayed({
            progressDialog.dismiss()
            askNotificationPermission() },2000)
    }
    private fun setObject(){
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
        if (Utils.getNameMonth()==""){
            binding.btnMonth.visibility = View.GONE
        }
        if (Utils.getNameYear()==""){
            binding.btnYear.visibility = View.GONE
        }
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
                val animAlpha = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_top_to_bottom)
                binding.userTab.drawer.startAnimation(animAlpha)
            }
        })
        binding.userTab.drawer.setOnDrawerCloseListener(object :MultiDirectionSlidingDrawer.OnDrawerCloseListener{
            override fun onDrawerClosed() {
                Utils.setOpenProfile(false)
                binding.RlHead.visibility = View.GONE
                binding.userTab.handle.setImageResource(R.mipmap.ic_slide_close)
                val animAlpha = AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_top_to_bottom)
                binding.userTab.drawer.startAnimation(animAlpha)
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
            binding.btnZodiac12.isClickable = false
            bannerShow!!.showPopupBanner(6,object :BannerShow.onAdClosed{
                override fun onAdClosed() {
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_mainFragment_to_menuZodiacFragment)
                    binding.btnZodiac12.isClickable = true
                }
            })
        }
        binding.btnToDay.setOnClickListener {
            if (ChkInternet(requireActivity()).isOnline){
                binding.btnToDay.isClickable = false
                bannerShow!!.showPopupBanner(6,object :BannerShow.onAdClosed{
                    override fun onAdClosed() {
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_mainFragment_to_zodiacTodayFragment)
                        binding.btnToDay.isClickable = true
                    }
                })
            }else{
                Toast.makeText(requireActivity(),getString(R.string.text_nonet_thai), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnWeek.setOnClickListener {
            Utils.setWebTitle(getString(R.string.zodiac_week))
            Utils.setWebUrl(Url.weekUrl)
            if (ChkInternet(requireActivity()).isOnline){
                binding.btnWeek.isClickable = false
                bannerShow!!.showPopupBanner(6,object :BannerShow.onAdClosed{
                    override fun onAdClosed() {
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
                        binding.btnWeek.isClickable = true
                    }
                })
            }else{
                Toast.makeText(requireActivity(),getString(R.string.text_nonet_thai), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnMonth.setOnClickListener {
            Utils.setWebTitle(getString(R.string.zodiac_month))
            Utils.setWebUrl(Url.monthUrl)
            if (ChkInternet(requireActivity()).isOnline){
                binding.btnMonth.isClickable = false
                bannerShow!!.showPopupBanner(6,object :BannerShow.onAdClosed{
                    override fun onAdClosed() {
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
                        binding.btnMonth.isClickable = true
                    }
                })
            }else{
                Toast.makeText(requireActivity(),getString(R.string.text_nonet_thai), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnYear.setOnClickListener {
            Utils.setWebTitle(getString(R.string.zodiac_year))
            Utils.setWebUrl(Url.yearUrl)
            if (ChkInternet(requireActivity()).isOnline){
                binding.btnYear.isClickable = false
                bannerShow!!.showPopupBanner(6,object :BannerShow.onAdClosed{
                    override fun onAdClosed() {
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_mainFragment_to_zodiacWebFragment)
                        binding.btnYear.isClickable = true
                    }
                })
            }else{
                Toast.makeText(requireActivity(),getString(R.string.text_nonet_thai), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnRecommend.setOnClickListener {
            if (ChkInternet(requireActivity()).isOnline){
                binding.btnRecommend.isClickable = false
                bannerShow!!.showPopupBanner(6,object :BannerShow.onAdClosed{
                    override fun onAdClosed() {
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_mainFragment_to_zodiacRecommend)
                        binding.btnRecommend.isClickable = true
                    }
                })
            }else{
                Toast.makeText(requireActivity(),getString(R.string.text_nonet_thai), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setNoticeAds(){
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
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
//            Log.e("checkSelfPermission","True")
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
//            Log.e("checkSelfPermission","else if")
            } else {
                // Directly ask for the permission
//            Log.e("checkSelfPermission","else")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    fun alertDialogExit(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(getString(R.string.text_exit))
        builder.setPositiveButton(getString(R.string.text_ok),object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                requireActivity().finish()
            }
        })
        builder.setNeutralButton(getString(R.string.text_no),object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                backAble = true
                p0?.dismiss()
            }
        })
        builder.setCancelable(false)
        builder.create()
        val dialog = builder.show()
        val tvMessage = dialog.findViewById<TextView>(android.R.id.message)
        tvMessage.gravity = Gravity.CENTER
        tvMessage.minHeight = 120
        dialog.show()
    }

    override fun onStart() {
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
        super.onStart()
    }

    override fun onResume() {
        bannerShow!!.loadPopupBanner(0)
        bannerShow!!.getShowBannerSmall(0)
        try {
            val path = requireActivity().getExternalFilesDir(null)
            val mFileTemp = File(path,imgUserFile)
            if (mFileTemp.exists()) {
                val myBitmap = BitmapFactory.decodeFile(mFileTemp.path)
                binding.userTab.mImguser.setImageBitmap(myBitmap)
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        super.onResume()
    }
}