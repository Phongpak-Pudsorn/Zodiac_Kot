package com.smileapp.zodiac.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
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
    var newsAdapter:NewsAdapter?=null
    var newsList = ArrayList<NewsInfo>()
    var refresh = true
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
        Font().styleText_RSU_BOLD(requireActivity(),binding.TvTitle,32)
        binding.imgBack.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigateUp()
        }
        CallData()
        binding.list.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false)
        }
    }
    fun CallData(){
        executor.execute {
            val service = ApiClient().getClient().create(Api::class.java)
//            service.getNews(url).enqueue(object : Callback<String>{
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    val data = Gson().toJson(response.body(),NewsInfo::class.java)
//                    Log.e("data",data.toString())
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.e("Failure",""+t.message)
//                }
//            })
            if (refresh) {
                newsList.clear()
                service.getPullup().enqueue(object : Callback<NewsInfo> {
                    override fun onResponse(call: Call<NewsInfo>, response: Response<NewsInfo>) {
                        if (response.isSuccessful) {
                            val data = Gson().toJson(response.body(), NewsInfo::class.java)
                            Log.e("data", data.toString())
                            newsList.add(response.body()!!)
                            Log.e("newsList", newsList.toString())
                            newsAdapter = NewsAdapter(requireActivity(),newsList[0].Datarow, bannerShow!!)
                        }
                    }

                    override fun onFailure(call: Call<NewsInfo>, t: Throwable) {
                        Log.e("Failure", "" + t.message)
                    }
                })
            }else{
                service.getPulldown().enqueue(object : Callback<NewsInfo> {
                    override fun onResponse(call: Call<NewsInfo>, response: Response<NewsInfo>) {
                        if (response.isSuccessful) {
                            val data = Gson().toJson(response.body(), NewsInfo::class.java)
                            Log.e("data", data.toString())
                            newsList.add(response.body()!!)
                            Log.e("newsList", newsList.toString())
                            newsAdapter = NewsAdapter(requireActivity(),newsList[0].Datarow, bannerShow!!)
                        }
                    }
                    override fun onFailure(call: Call<NewsInfo>, t: Throwable) {
                        Log.e("Failure", "" + t.message)
                    }
                })
            }
            handler.post {


            }
        }
    }
}