package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.smileapp.zodiac.MyApplication
import com.smileapp.zodiac.adapter.MenuMainAdapter
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentMenuZodiacBinding
import com.smileapp.zodiac.model.ZodiacInfo
import com.smileapp.zodiac.utils.Utils
import org.json.JSONException
import java.util.concurrent.Executors

class MenuZodiacFragment:Fragment() {
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var zodiacMain = ArrayList<ZodiacInfo.ZodiacData.MainData>()
    var bannerShow:BannerShow?=null
    val binding:FragmentMenuZodiacBinding by lazy { FragmentMenuZodiacBinding.inflate(layoutInflater) }
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
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigateUp()
        }
        CallData()
    }
    fun CallData(){
        executor.execute {
            val menuStr = Utils.loadFromAssets(requireActivity())
            zodiacMain.clear()
            try {
                val menuData = Gson().fromJson(menuStr,ZodiacInfo::class.java)
                for (i in menuData.Data_Zodiac.zodiac_main.indices) {
                    zodiacMain.add(menuData.Data_Zodiac.zodiac_main[i])
                }
                Log.e("zodiacMain size",zodiacMain.size.toString())
                Log.e("zodiacMain size",zodiacMain.toString())

            }catch (e: JSONException){
                e.printStackTrace()
            }
            handler.post{
//                mMenuMainAdapter?.notifyDataSetChanged()
                binding.menuView.apply {
                    layoutManager = GridLayoutManager(MyApplication.getContext(),2,RecyclerView.VERTICAL,false)
                    adapter = MenuMainAdapter(requireActivity(),zodiacMain, bannerShow!!)
                }
            }
        }

    }
//    fun setColumnGridView() {
//        val displaymetrics = DisplayMetrics()
//        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.R){
//            requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
//            screenWight = displaymetrics.widthPixels / 2
//        }else{
//            requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
//            screenWight = displaymetrics.widthPixels / 2
//        }
//    }

    override fun onStart() {
        bannerShow = BannerShow(requireActivity(),Utils.UUID)
        super.onStart()
    }

    override fun onResume() {
        bannerShow!!.loadPopupBanner(0)
        bannerShow!!.getShowBannerSmall(10)
        super.onResume()
    }
}