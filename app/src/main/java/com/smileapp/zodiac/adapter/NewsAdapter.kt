package com.smileapp.zodiac.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.commonclass.Font
import com.smileapp.zodiac.databinding.AdsNativeBinding
import com.smileapp.zodiac.databinding.ItemListBinding
import com.smileapp.zodiac.model.NewsInfo

class NewsAdapter(val mContext:Context,val list:ArrayList<NewsInfo.DataInfo>,private val bannerShow: BannerShow):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(val newsBinding:ItemListBinding):RecyclerView.ViewHolder(newsBinding.root)
    class NativeHolder(val nativebinding: AdsNativeBinding):RecyclerView.ViewHolder(nativebinding.root)
    val VIEW_TYPE_ITEM = 1
    val VIEW_TYPE_NATIVE = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==VIEW_TYPE_ITEM){
            val binding=ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(binding)
        }else{
            val binding=AdsNativeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            bannerShow.showNativeList(7.toString(),1,binding.root)
            return NativeHolder(binding)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        if(list[position].artide_date=="abcdef"){
            return VIEW_TYPE_NATIVE
        }else{
            return VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
            try {
                holder.newsBinding.txtDate.text = list[position].artide_date
                holder.newsBinding.txtTitle.text = list[position].title
                Font().styleText_RSU_BOLD(mContext,holder.newsBinding.txtTitle,26)
                holder.newsBinding.txtTotalView.text = list[position].total_view
                Glide.with(mContext)
                    .load(list[position].image)
                    .placeholder(R.mipmap.img_load_default)
                    .into(holder.newsBinding.imageView)
                if (list[position].pin){
                    holder.newsBinding.txtTitle.setTextColor(Color.parseColor("#FFFFFF"))
                    holder.newsBinding.txtDate.setTextColor(Color.parseColor("#000000"))
                    holder.newsBinding.txtTotalView.setTextColor(Color.parseColor("#000000"))
                    holder.newsBinding.imgViewEye.setImageResource(R.mipmap.eyeview_black)
                    holder.newsBinding.linearLayout1.setBackgroundResource(R.drawable.click_item_news_hightlight)
                }else{
                    holder.newsBinding.txtTitle.setTextColor(Color.parseColor("#000000"))
                    holder.newsBinding.txtDate.setTextColor(Color.parseColor("#888484"))
                    holder.newsBinding.txtTotalView.setTextColor(Color.parseColor("#888484"))
                    holder.newsBinding.imgViewEye.setImageResource(R.mipmap.eyeview)
                    holder.newsBinding.linearLayout1.setBackgroundResource(R.drawable.click_item_news)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }else{
            val nativeHolder = holder as NativeHolder
        }
    }
}