package com.smileapp.zodiac.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.databinding.ItemMenuMainBinding
import com.smileapp.zodiac.model.ZodiacInfo
import com.smileapp.zodiac.utils.Utils

class MenuMainAdapter(val context: Context,val listData:ArrayList<ZodiacInfo.ZodiacData.MainData>,val bannerShow: BannerShow):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var click = false

    class ViewHolder(val menuBinding: ItemMenuMainBinding): RecyclerView.ViewHolder(menuBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMenuMainBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (holder is ViewHolder){
            val imgName = listData[position].zodiac_image.replace(".png","")
            val img = context.resources.getIdentifier(imgName,"mipmap",context.packageName)
            holder.menuBinding.TvNameZodiacTH.text =listData[position].zodiac_name_thai
            holder.menuBinding.TvNameZodiacEN.text =listData[position].zodiac_name_eng
            holder.menuBinding.TvNameZodiacDate.text =listData[position].zodiac_date
            holder.menuBinding.imgMenu.setBackgroundResource(img)
            holder.menuBinding.imgMenu.setOnClickListener{v->
                val animAlpha = AnimationUtils.loadAnimation(
                    context, R.anim.move_y2
                )
                if (!click) {
                    click = true
                    v.startAnimation(animAlpha)
                    Utils.menuPosition = position
                    val timer = object : CountDownTimer(1000, 1000) {
                        override fun onTick(p0: Long) {
                        }

                        override fun onFinish() {
                            Utils.setPredictPosition(position)
                                bannerShow.showPopupBanner(2, object : BannerShow.onAdClosed {
                                    override fun onAdClosed() {
                                        click = false
                                        Utils.showBanner = false
                                        Navigation.findNavController(v)
                                            .navigate(R.id.action_menuZodiacFragment_to_predictFragment)
                                    }
                                })
                        }

                    }
                    timer.start()
                }
            }

        }
    }
}