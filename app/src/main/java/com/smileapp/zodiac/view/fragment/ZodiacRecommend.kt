package com.smileapp.zodiac.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.smileapp.zodiac.R
import com.smileapp.zodiac.adapter.NewsAdapter
import com.smileapp.zodiac.api.Api
import com.smileapp.zodiac.api.ApiClient
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.FragmentRecommendBinding
import com.smileapp.zodiac.model.NewsInfo
import com.smileapp.zodiac.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class ZodiacRecommend:Fragment() {
    var newsList = ArrayList<NewsInfo.DataInfo>()
    var nativeList = ArrayList<NewsInfo.DataInfo>()
    var newsAdapter:NewsAdapter?=null
    var isLoading = false
    val pullup = "&pullup=false"
    val pulldown = "&pullup=true"
    var bannerShow :BannerShow?=null
    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    val binding: FragmentRecommendBinding by lazy { FragmentRecommendBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bannerShow = BannerShow(requireActivity(), Utils.UUID)
        bannerShow!!.loadPopupBanner(0)
        bannerShow!!.getShowBannerSmall(10)
        nativeList = arrayListOf(NewsInfo.DataInfo("abcdef","","","","","","",false))
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)

        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigateUp()
        }
        initial()
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireActivity(),R.color.google_blue),
            ContextCompat.getColor(requireActivity(),R.color.google_green),
            ContextCompat.getColor(requireActivity(),R.color.google_red),
            ContextCompat.getColor(requireActivity(),R.color.google_yellow))
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.list.visibility = View.INVISIBLE
            loadMore()
            //                simulateLoadingData();
            val countDownTimer = object : CountDownTimer(2000, 1000) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    binding.list.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
            countDownTimer.start()
        }
        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isLoading){
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == newsList.size - 1){
                        isLoading = true
                        loadMore()
                        recyclerView.scrollToPosition(newsList.size-1)
                    }

                }
            }
        })
    }

    fun initial(){
            val service = ApiClient().getClient().create(Api::class.java)
                newsList.clear()
                service.getPullup().enqueue(object : Callback<NewsInfo> {
                    override fun onResponse(call: Call<NewsInfo>, response: Response<NewsInfo>) {
                        if (response.isSuccessful) {
                            val strData = Gson().toJson(response.body(), NewsInfo::class.java)
                            Log.e("data pullup", strData.toString())
                            val data = Gson().fromJson(strData,NewsInfo::class.java)
                            Log.e("strdata pullup", strData.toString())
                            for (i in data.Datarow.indices) {
                                newsList.add(data.Datarow[i])
                            }
                            var j = 0
                            for (i in newsList.indices){
                                if (j==7){
                                    if (newsList[i].artide_id!="abcdef") {
                                        newsList.addAll(i, nativeList)
                                    }
                                    j=0
                                }else{
                                    j+=1
                                }
                            }
                            Log.e("newsList pullup", newsList.toString())
                            newsAdapter = NewsAdapter(requireActivity(),newsList, bannerShow!!)
                            binding.list.apply {
                                adapter = newsAdapter
                                layoutManager = LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false)
                            }
                        }
                    }

                    override fun onFailure(call: Call<NewsInfo>, t: Throwable) {
                        Log.e("Failure", "" + t.message)
                    }
                })
    }
    fun loadMore(){
        val service = ApiClient().getClient().create(Api::class.java)
        service.getPulldown().enqueue(object : Callback<NewsInfo> {
            override fun onResponse(call: Call<NewsInfo>, response: Response<NewsInfo>) {
                if (response.isSuccessful) {
                    val strData = Gson().toJson(response.body(), NewsInfo::class.java)
                    Log.e("data pulldown", strData.toString())
                    val data = Gson().fromJson(strData,NewsInfo::class.java)
                    Log.e("strdata pulldown", strData.toString())
                    for (i in data.Datarow.indices) {
                        newsList.add(data.Datarow[i])
                    }
                    var j = 0
                    for (i in newsList.indices){
                        if (j==7){
                            if (newsList[i].artide_id!="abcdef") {
                                newsList.addAll(i, nativeList)
                            }
                            j=0
                        }else{
                            j+=1
                        }
                    }
                    Log.e("newsList pulldown", newsList.toString())
                    newsAdapter?.notifyDataSetChanged()
//                    newsAdapter = NewsAdapter(requireActivity(),newsList, bannerShow!!)
//                    binding.list.apply {
//                        adapter = newsAdapter
//                        layoutManager = LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false)
//                    }
                }
            }
            override fun onFailure(call: Call<NewsInfo>, t: Throwable) {
                Log.e("Failure", "" + t.message)
            }
        })
        isLoading = false
    }
}