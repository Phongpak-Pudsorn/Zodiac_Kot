package com.smileapp.zodiac.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.smileapp.zodiac.MyApplication
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentPredictBinding
import com.smileapp.zodiac.model.ZodiacInfo
import com.smileapp.zodiac.utils.Utils
import org.json.JSONException
import java.util.concurrent.Executors

class PredictFragment:Fragment() {
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var zodiacMain = ArrayList<ZodiacInfo.ZodiacData.MainData>()
    var zodiacGeneral = ArrayList<ZodiacInfo.ZodiacData.GeneralData.GeneralDetails>()
    var zodiacLove = ArrayList<ZodiacInfo.ZodiacData.LoveData.LoveDetails>()
    var bannerShow:BannerShow?=null
    var beforeClick = 0
    val binding:FragmentPredictBinding by lazy { FragmentPredictBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvNameRasi,30)
        Utils.setTextGradient_Menu_Black_Blue(binding.TvNameRasi)
        callData()
        Log.e("oncreated zodiacMain",zodiacMain.toString())
        Log.e("oncreated zodiacGeneral",zodiacGeneral.toString())
        binding.imgShare.setOnClickListener {
            bannerShow!!.showPopupBanner(3,object :BannerShow.onAdClosed{
                override fun onAdClosed() {
                    Navigation.findNavController(view).navigate(R.id.action_predictFragment_to_shareFragment)
                }
            })
        }
    }
    fun callData(){
        executor.execute {
            val menuStr = Utils.loadFromAssets(requireActivity())
            zodiacGeneral.clear()
            zodiacMain.clear()
            zodiacLove.clear()
            try {
                val menuData = Gson().fromJson(menuStr, ZodiacInfo::class.java)
                zodiacMain.add(menuData.Data_Zodiac.zodiac_main[Utils.getPredictPosition()])
                when(Utils.getPredictPosition()){
                    0-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_1)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_1)
                    }
                    1-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_2)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_2)

                    }
                    2-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_3)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_3)
                    }
                    3-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_4)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_4)
                    }
                    4-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_5)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_5)
                    }
                    5-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_6)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_6)
                    }
                    6-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_7)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_7)
                    }
                    7-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_8)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_8)
                    }
                    8-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_9)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_9)
                    }
                    9-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_10)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_10)
                    }
                    10-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_11)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_11)
                    }
                    11-> {
                        zodiacGeneral.addAll(menuData.Data_Zodiac.zodiac_general.zodiac_12)
                        zodiacLove.addAll(menuData.Data_Zodiac.zodiac_love.zodiac_12)
                    }
                }
                Log.e("zodiacMain size",zodiacMain.size.toString())
                Log.e("zodiacMain size",zodiacMain.toString())
                Log.e("zodiacGeneral",zodiacGeneral.toString())
                Log.e("zodiacGeneral size",zodiacGeneral.size.toString())
                binding.TvNameRasi.text = zodiacMain[0].zodiac_name_thai+" "+zodiacMain[0].zodiac_name_eng+"\n"+zodiacMain[0].zodiac_date
                val name = zodiacMain[0].zodiac_image.replace(".png","")
                Utils.setPredictRasi(name)
                Utils.setPredictName(zodiacMain[0].zodiac_name_thai+" "+zodiacMain[0].zodiac_name_eng+"\n"+zodiacMain[0].zodiac_date)
                val img = requireActivity().resources.getIdentifier(name,"mipmap",requireActivity().packageName)
                binding.imgDodiac.setImageResource(img)
            }catch (e: JSONException){
                e.printStackTrace()
            }
            handler.post {
                beforeClick = binding.RdgMenuTop.checkedRadioButtonId
                setBottomTab(zodiacGeneral,zodiacLove,0)
                binding.rdGender.setOnClickListener {
                    if (binding.RdgMenuTop.checkedRadioButtonId!=beforeClick) {
                        setBottomTab(zodiacGeneral, zodiacLove, 0)
                        beforeClick = binding.RdgMenuTop.checkedRadioButtonId
                    }
                }
                binding.rdLove.setOnClickListener {
                    if (binding.RdgMenuTop.checkedRadioButtonId!=beforeClick) {
                        setBottomTab(zodiacGeneral, zodiacLove, 1)
                        beforeClick = binding.RdgMenuTop.checkedRadioButtonId
                    }
                }

            }
        }

    }
    fun setBottomTab(generalData:ArrayList<ZodiacInfo.ZodiacData.GeneralData.GeneralDetails>,loveData:ArrayList<ZodiacInfo.ZodiacData.LoveData.LoveDetails>,type:Int){
        binding.hourRadioGroup.removeAllViews()
        val dm = resources.displayMetrics
        val screenWight = dm.widthPixels
        val width = if (type==0){
            screenWight/generalData.size
        }else{
            screenWight/loveData.size
        }
        val params = LinearLayout.LayoutParams(width,RadioGroup.LayoutParams.MATCH_PARENT)
        binding.RlWebView.removeAllViews()
        val mWvDescription = WebView(MyApplication.getContext())
        mWvDescription.setBackgroundColor(Color.TRANSPARENT)
        mWvDescription.isVerticalFadingEdgeEnabled = false
        if (type==0) {
            mWvDescription.loadDataWithBaseURL(
                null,
                Utils.getHtmlData(requireActivity(), generalData[0].zodiac_description, 28)!!,
                "text/html",
                "utf-8",
                null
            )
            Utils.setPredictMenu(generalData[0].zodiac_menu)
            Utils.setPredictDesc(generalData[0].zodiac_description)
        }else{
            mWvDescription.loadDataWithBaseURL(
                null,
                Utils.getHtmlData(requireActivity(), loveData[0].zodiac_description, 28)!!,
                "text/html",
                "utf-8",
                null
            )
            Utils.setPredictMenu(loveData[0].zodiac_menu)
            Utils.setPredictDesc(loveData[0].zodiac_description)
        }
        binding.RlWebView.addView(mWvDescription)
        if (type==0) {
            for (i in generalData.indices) {
                val button = RadioButton(requireActivity())
                button.id = i
                if (button.id == 0) {
                    button.isChecked = true
                }
                button.setPadding(10, 10, 10, 10)
                button.gravity = Gravity.CENTER
                button.layoutParams = params
                button.textSize = 12f
                button.setTextColor(Color.WHITE)
                button.setButtonDrawable(R.color.transparent)
                button.text = generalData[i].zodiac_menu
                button.setBackgroundResource(R.drawable.bg_menu1)
                button.setOnClickListener {
                    binding.RlWebView.removeAllViews()
                    mWvDescription.setBackgroundColor(Color.TRANSPARENT)
                    mWvDescription.loadDataWithBaseURL(
                        null,
                        Utils.getHtmlData(
                            requireActivity(),
                            generalData[button.id].zodiac_description,
                            28
                        )!!,
                        "text/html",
                        "utf-8",
                        null
                    )
                    Utils.setPredictMenu(generalData[button.id].zodiac_menu)
                    Utils.setPredictDesc(generalData[button.id].zodiac_description)
                    binding.RlWebView.addView(mWvDescription)
                }
                binding.hourRadioGroup.addView(button)
            }
        }else{
            for (i in loveData.indices){
                val button = RadioButton(requireActivity())
                button.id = i
                if (button.id==0){
                    button.isChecked = true
                }
                button.setPadding(10,10,10,10)
                button.gravity =Gravity.CENTER
                button.layoutParams = params
                button.textSize = 12f
                button.setTextColor(Color.WHITE)
                button.setButtonDrawable(R.color.transparent)
                button.text = loveData[i].zodiac_menu
                button.setBackgroundResource(R.drawable.bg_menu2)
                button.setOnClickListener {
                    binding.RlWebView.removeAllViews()
                    mWvDescription.setBackgroundColor(Color.TRANSPARENT)
                    mWvDescription.loadDataWithBaseURL(null, Utils.getHtmlData(requireActivity(),loveData[button.id].zodiac_description,28)!!, "text/html", "utf-8", null)
                    Utils.setPredictMenu(loveData[button.id].zodiac_menu)
                    Utils.setPredictDesc(loveData[button.id].zodiac_description)
                    binding.RlWebView.addView(mWvDescription)
                }
                binding.hourRadioGroup.addView(button)
            }
        }
    }

    override fun onStart() {
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
        super.onStart()
    }

    override fun onResume() {
        binding.RdgMenuTop.check(binding.rdGender.id)
        bannerShow!!.loadPopupBanner(0)
        bannerShow!!.getShowBannerSmall(10)
        super.onResume()
    }
}