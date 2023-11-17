package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.smileapp.zodiac.MyApplication
import com.smileapp.zodiac.R
import com.smileapp.zodiac.ZodiacInfo
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentProfileBinding
import com.smileapp.zodiac.databinding.FragmentSplashBinding
import com.smileapp.zodiac.utils.Utils

class ProfileFragment:Fragment() {
    var items = ArrayList<String>()
    var checkUserDataNull = false
    val binding: FragmentProfileBinding by lazy { FragmentProfileBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input = requireActivity().assets.open("DataZodiac/Data_Zodiac.json")
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        val zodiacStr = String(buffer, charset("UTF-8"))
//        Log.e("Zodiac",zodiacStr)
        val zodiacData = Gson().fromJson(zodiacStr,ZodiacInfo::class.java)
        val zodiacMain = zodiacData.Data_Zodiac.zodiac_main
//        Log.e("Zodiac",zodiacData.Data_Zodiac.zodiac_main[0].zodiac_id)
        for (i in zodiacMain.indices){
            items.add(zodiacMain[i].zodiac_name_thai+" "+zodiacMain[i].zodiac_name_eng+" "+zodiacMain[i].zodiac_date)
        }
        Log.e("items",items.toString())
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        Utils.setTextGradient_Blue(binding.TvZodiac)
        Utils.setTextGradient_Blue(binding.TvName)
        Utils.setTextGradient_Blue(binding.TvGender)
        binding.ImgStart.setOnClickListener {
            Utils.setNameUser(binding.mEdittext.toString())
            if (Utils.getNameUser()==""){
                Toast.makeText(MyApplication.getContext(),"กรุณาใส่ชื่อของคุณ",Toast.LENGTH_SHORT).show()
                checkUserDataNull = true
            }else{
                checkUserDataNull = false
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_profileFragment_to_mainFragment)
            }
        }
    }
}