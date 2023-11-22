package com.smileapp.zodiac.view.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.smileapp.zodiac.R
import com.smileapp.zodiac.api.Api
import com.smileapp.zodiac.api.ApiClient
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentTodayBinding
import com.smileapp.zodiac.model.ZodiacTodayInfo
import com.smileapp.zodiac.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ZodiacTodayFragment:Fragment() {
    private var strDescription = ""
    private var strDescription_share = ""
    val binding:FragmentTodayBinding by lazy { FragmentTodayBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var listData = ArrayList<ZodiacTodayInfo>()
        val service = ApiClient().getClient().create(Api::class.java)
        service.getToday().enqueue(object: Callback<ZodiacTodayInfo>{
            override fun onResponse(
                call: Call<ZodiacTodayInfo>,
                response: Response<ZodiacTodayInfo>,
            ) {
                if (response.isSuccessful){
                    listData.clear()
                    listData.addAll(listOf(response.body()!!))
                    Log.e("listData",listData.toString())
                    setToday(listData)
                }else{

                }
            }

            override fun onFailure(call: Call<ZodiacTodayInfo>, t: Throwable) {
                Log.e("Failure",""+t.message)
            }
        })
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigateUp()
        }
        binding.imgShare.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_zodiacTodayFragment_to_shareTodayFragment)
        }
    }
    fun setToday(list:ArrayList<ZodiacTodayInfo>){
        Utils.setStringImageDrawable(requireActivity(),binding.imgDay,"day0",300,215)
        binding.hourRadioGroup.removeAllViews()
        val dm = resources.displayMetrics
        val screenWidth = dm.widthPixels/list[0].Datarow.zodiactoday.size
        val params = LinearLayout.LayoutParams(screenWidth,RadioGroup.LayoutParams.MATCH_PARENT)

        strDescription = "<p  style=\"line-height:100%\">"+"<font size=\"6\" color=\"blue\" ><b>การงาน :</b></font><br>"+list[0].Datarow.zodiactoday[0].work+"</p>"+"<p  style=\"line-height:100%\"><font   size=\"6\" color=\"#d0ac1a\" ><b>การเงิน :</b></font><br>"+list[0].Datarow.zodiactoday[0].money+"</p>"+"<p  style=\"line-height:100%\"><font  size=\"6\" color=\"#FF4081\" ><b>ความรัก :</b></font><br>"+list[0].Datarow.zodiactoday[0].love+"</p>"+"<font size=\"3\" >"+list[0].Datarow.credit+"</font>"
        strDescription_share = "<p  style=\"line-height:100%\">"+"<font size=\"6\" color=\"blue\" ><b>การงาน :</b></font><br>"+list[0].Datarow.zodiactoday[0].work+"</p>"+"<p  style=\"line-height:100%\"><font   size=\"6\" color=\"#d0ac1a\" ><b>การเงิน :</b></font><br>"+list[0].Datarow.zodiactoday[0].money+"</p>"+"<p  style=\"line-height:100%\"><font  size=\"6\" color=\"#FF4081\" ><b>ความรัก :</b></font><br>"+list[0].Datarow.zodiactoday[0].love+"</p>"
        binding.TvDailydate.text = list[0].Datarow.daily_date
        binding.RlWebView.removeAllViews()
        val webView = WebView(requireActivity())
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.isVerticalFadingEdgeEnabled = false
        webView.loadDataWithBaseURL(null, Utils.getHtmlData(requireActivity(),strDescription,28)!!,"text/html", "utf-8", null)
        binding.RlWebView.addView(webView)
        var idDay = 0
        Utils.setSharedDay(list[0].Datarow.daily_date)
        Utils.setSharedID(idDay)
        Utils.setSharedImage("day0")
        Utils.setSharedDesc(strDescription)
        for (i in list[0].Datarow.zodiactoday.indices){
            val button = RadioButton(requireActivity())
            button.id = i
            if (button.id==0){
                button.isChecked = true
            }
            button.setPadding(10,10,10,10)
            button.gravity = Gravity.CENTER
            button.layoutParams = params
            button.textSize = 32f
            button.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            button.typeface = Typeface.DEFAULT_BOLD
            button.setButtonDrawable(R.color.transparent)
            val type = Typeface.createFromAsset(requireActivity().assets,"fonts/psl030pro.ttf")
            button.typeface = type
            button.setShadowLayer(0.01f,-2f,2f,ContextCompat.getColor(requireActivity(),R.color.black))
            when(i){
                0 -> {
                    button.setText(getString(R.string.sun))
                    button.setBackgroundResource(R.drawable.bg_day_0)
                }
                1 -> {
                    button.setText(getString(R.string.mon))
                    button.setBackgroundResource(R.drawable.bg_day_1)
                }
                2 -> {
                    button.setText(getString(R.string.tue))
                    button.setBackgroundResource(R.drawable.bg_day_2)
                }
                3 -> {
                    button.setText(getString(R.string.wed))
                    button.setBackgroundResource(R.drawable.bg_day_3)
                }
                4 -> {
                    button.setText(getString(R.string.thu))
                    button.setBackgroundResource(R.drawable.bg_day_4)
                }
                5 -> {
                    button.setText(getString(R.string.fri))
                    button.setBackgroundResource(R.drawable.bg_day_5)
                }
                6 -> {
                    button.setText(getString(R.string.sat))
                    button.setBackgroundResource(R.drawable.bg_day_6)
                }

            }
            button.setOnClickListener {
                binding.RlWebView.removeAllViews()
                idDay = binding.hourRadioGroup.checkedRadioButtonId
                Utils.setStringImageDrawable(requireActivity(),binding.imgDay, "day$idDay",300,215)
                val webView = WebView(requireActivity())
                webView.setBackgroundColor(Color.TRANSPARENT)
                webView.isVerticalFadingEdgeEnabled = false
                webView.loadDataWithBaseURL(null, Utils.getHtmlData(requireActivity(),strDescription,28)!!,"text/html", "utf-8", null)
                binding.RlWebView.addView(webView)
                strDescription = "<p  style=\"line-height:100%\">"+"<font size=\"6\" color=\"blue\" ><b>การงาน :</b></font><br>"+list[0].Datarow.zodiactoday[idDay].work+"</p>"+"<p  style=\"line-height:100%\"><font   size=\"6\" color=\"#d0ac1a\" ><b>การเงิน :</b></font><br>"+list[0].Datarow.zodiactoday[idDay].money+"</p>"+"<p  style=\"line-height:100%\"><font  size=\"6\" color=\"#FF4081\" ><b>ความรัก :</b></font><br>"+list[0].Datarow.zodiactoday[idDay].love+"</p>"+"<font size=\"3\" >"+list[0].Datarow.credit+"</font>"
                strDescription_share = "<p  style=\"line-height:100%\">"+"<font size=\"6\" color=\"blue\" ><b>การงาน :</b></font><br>"+list[0].Datarow.zodiactoday[idDay].work+"</p>"+"<p  style=\"line-height:100%\"><font   size=\"6\" color=\"#d0ac1a\" ><b>การเงิน :</b></font><br>"+list[0].Datarow.zodiactoday[idDay].money+"</p>"+"<p  style=\"line-height:100%\"><font  size=\"6\" color=\"#FF4081\" ><b>ความรัก :</b></font><br>"+list[0].Datarow.zodiactoday[idDay].love+"</p>"
                webView.loadDataWithBaseURL(null, Utils.getHtmlData(requireActivity(),strDescription,30)!!,"text/html", "utf-8", null)
                Utils.setSharedDay(list[0].Datarow.daily_date)
                Utils.setSharedID(idDay)
                Utils.setSharedImage("day$idDay")
                Utils.setSharedDesc(strDescription)
            }
            binding.hourRadioGroup.addView(button)
        }
    }
}