package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.smileapp.zodiac.MyApplication
import com.smileapp.zodiac.R
import com.smileapp.zodiac.adapter.SpinnerAdapter
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentProfileBinding
import com.smileapp.zodiac.info.ZodiacInfo
import com.smileapp.zodiac.utils.Utils
import com.starvision.bannersdk.NoticeAds
import org.json.JSONException
import java.util.concurrent.Executors

class ProfileFragment:Fragment() {
    var zodiacMain:ArrayList<ZodiacInfo.ZodiacData.MainData>?=null
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var items = ArrayList<String>()
    var checkUserDataNull = false
    val binding: FragmentProfileBinding by lazy { FragmentProfileBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CallData()
        setNoticeAds()
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        Utils.setTextGradient_Blue(binding.TvZodiac)
        Utils.setTextGradient_Blue(binding.TvName)
        Utils.setTextGradient_Blue(binding.TvGender)
        binding.ImgStart.setOnClickListener {
            val gender = binding.rdGender.checkedRadioButtonId
            if (Utils.getNameUser()==""){
                Toast.makeText(MyApplication.getContext(),"กรุณาใส่ชื่อของคุณ",Toast.LENGTH_SHORT).show()
                checkUserDataNull = true
            }else{
                checkUserDataNull = false
                if (gender==binding.rdMan.id){
                    Log.e("man",binding.rdMan.id.toString())
                    val strGender = "Man"
                    Utils.setGENDER(strGender)
                    checkUserDataNull = false
                }else if (gender==binding.rdWoman.id){
                    Log.e("woman",binding.rdWoman.id.toString())
                    val strGender = "Woman"
                    Utils.setGENDER(strGender)
                    checkUserDataNull = false
                }else{
                    Toast.makeText(MyApplication.getContext(),"กรุณาระบุเพศของคุณ",Toast.LENGTH_SHORT).show()
                    checkUserDataNull = true
                }
            }
            if(!checkUserDataNull){
                Utils.setNameAndDateRasi(zodiacMain!![Utils.getRasi()].zodiac_name_thai+" "+zodiacMain!![Utils.getRasi()].zodiac_name_eng+"\n"+zodiacMain!![Utils.getRasi()].zodiac_date)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_profileFragment_to_mainFragment)
            }
        }
        binding.SpinDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                Utils.setRasi(position)
                // mySpinner.setSelection(mAppPreference.getRari());
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    fun CallData(){
        executor.execute {
            val zodiacStr = Utils.loadFromAssets(requireActivity())
            items.clear()
            try {
                val zodiacData = Gson().fromJson(zodiacStr, ZodiacInfo::class.java)
                zodiacMain = zodiacData.Data_Zodiac.zodiac_main

            }catch (e:JSONException){
                e.printStackTrace()
            }


            handler.post{
                if (zodiacMain!!.size >0) {
                    for (i in zodiacMain!!.indices) {
                        items.add(zodiacMain!![i].zodiac_name_thai + " " + zodiacMain!![i].zodiac_name_eng + " " + zodiacMain!![i].zodiac_date)
                    }
                    Log.e("CallData",items.toString())
                }
                binding.SpinDate.adapter = SpinnerAdapter(requireActivity(),R.layout.row,items)
            }
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