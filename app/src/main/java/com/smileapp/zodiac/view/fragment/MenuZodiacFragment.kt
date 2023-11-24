package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
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
    var mMenuMainAdapter: MenuMainAdapter? = null
    var zodiacMain = ArrayList<ZodiacInfo>()
    var screenWight = 100
    var items = ArrayList<String>()
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
        setColumnGridView()
        bannerShow = BannerShow(requireActivity(),Utils.UUID)
        bannerShow!!.getShowBannerSmall(10)
        bannerShow!!.loadPopupBanner(0)
        mMenuMainAdapter = MenuMainAdapter(requireActivity(),zodiacMain,screenWight,bannerShow)
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigateUp()
        }
        binding.mGridView.adapter = mMenuMainAdapter
        CallData()
    }
    fun CallData(){
        executor.execute {
            val menuStr = Utils.loadFromAssets(requireActivity())
            zodiacMain.clear()
            try {
                val menuData = Gson().fromJson(menuStr,ZodiacInfo::class.java)
                zodiacMain.add(menuData)

            }catch (e: JSONException){
                e.printStackTrace()
            }
            handler.post{
                mMenuMainAdapter?.notifyDataSetChanged()
            }
        }

    }
    fun setColumnGridView() {
        val displaymetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.R){
            requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
            screenWight = displaymetrics.widthPixels / 2
        }else{
            requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
            screenWight = displaymetrics.widthPixels / 2
        }
    }
}