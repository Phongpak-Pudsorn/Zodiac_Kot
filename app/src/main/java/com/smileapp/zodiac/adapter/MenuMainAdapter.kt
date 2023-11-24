package com.smileapp.zodiac.adapter

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smileapp.zodiac.R
import com.smileapp.zodiac.commonclass.BannerShow
import com.smileapp.zodiac.model.ZodiacInfo
import com.smileapp.zodiac.utils.Utils

class MenuMainAdapter(
    var context: Context,
    listData: ArrayList<ZodiacInfo>,
    screenWight: Int,
    bannerShow: BannerShow?,
) :
    BaseAdapter() {
    var holder: ViewHolder? = null
    var mInflater: LayoutInflater
    private var listData: ArrayList<ZodiacInfo> = ArrayList()
    var screenWight: Int
    private val bannerShow: BannerShow

    init {
        this.listData = listData
        this.screenWight = screenWight
        //  this.bannerShow = bannerShow;
        this.bannerShow = BannerShow(context as Activity, Utils.UUID)
        this.bannerShow.loadPopupBanner(0)
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder {
        // LinearLayout Li;
        var mImage: ImageView? = null
        var imgClick: ImageView? = null
        var TvNameZodiacTH: TextView? = null
        var TvNameZodiacEN: TextView? = null
        var TvNameZodiacDate: TextView? = null
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_menu_main, null)
            holder = ViewHolder()
            //   holder.Li = (LinearLayout) convertView.findViewById(R.id.Li);
//            holder.LiBG = (LinearLayout) convertView.findViewById(R.id.mLiBG);
            holder!!.TvNameZodiacTH =
                convertView.findViewById<View>(R.id.TvNameZodiacTH) as TextView
            holder!!.TvNameZodiacEN =
                convertView.findViewById<View>(R.id.TvNameZodiacEN) as TextView
            holder!!.TvNameZodiacDate =
                convertView.findViewById<View>(R.id.TvNameZodiacDate) as TextView
            val layoutImage = LinearLayout.LayoutParams(screenWight, screenWight)
            holder!!.mImage = convertView.findViewById<View>(R.id.imgMenu) as ImageView
            holder!!.imgClick = convertView.findViewById<View>(R.id.imgClick) as ImageView
            //holder.mImage .mImage(layoutImage);
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        convertView.setOnClickListener { v ->
            v.isEnabled = false
            Handler().postDelayed({ v.isEnabled = true }, 1000)
            bannerShow.showPopupBanner(2, object : BannerShow.onAdClosed {
                //                fun onAdClosed() {
//                    val intent = Intent(context, PredictActivity::class.java)
//                    intent.putExtra("DataMenu", false)
//                    intent.putExtra("IdRasi", listData[position].strZodiac_id)
//                    intent.putExtra("ImageRasi", listData[position].strZodiac_image)
//                    intent.putExtra(
//                        "NameAndDateRasi",
//                        (listData[position].strZodiac_name_thai + " " + listData[position].strZodiac_name_eng).toString() + "\n" + listData[position].strZodiac_date
//                    )
//                    context.startActivity(intent)
//                }
                override fun onAdClosed() {
                    TODO("Not yet implemented")
                }
            })
        }
        holder!!.mImage!!.setOnClickListener { v ->
            val animAlpha = AnimationUtils.loadAnimation(
                context, R.anim.move_y2
            )
            v.startAnimation(animAlpha)
            val myHandler = Handler()
            myHandler.postDelayed({
                bannerShow.showPopupBanner(2, object : BannerShow.onAdClosed {
                    //                    fun onAdClosed() {
//                        val intent = Intent(context, PredictActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                        intent.putExtra("DataMenu", false)
//                        intent.putExtra("IdRasi", listData[position].strZodiac_id)
//                        intent.putExtra("ImageRasi", listData[position].strZodiac_image)
//                        intent.putExtra(
//                            "NameAndDateRasi",
//                            (listData[position].strZodiac_name_thai + " " + listData[position].strZodiac_name_eng).toString() + "\n" + listData[position].strZodiac_date
//                        )
//                        context.startActivity(intent)
//                    }
                    override fun onAdClosed() {
                        TODO("Not yet implemented")
                    }
                })
            }, 300)
        }
        holder!!.TvNameZodiacTH!!.text =listData[0].Data_Zodiac.zodiac_main[position].zodiac_name_thai
        holder!!.TvNameZodiacEN!!.text=listData[0].Data_Zodiac.zodiac_main[position].zodiac_name_eng
        holder!!.TvNameZodiacDate!!.text=listData[0].Data_Zodiac.zodiac_main[position].zodiac_date
        // Font.styleText_THSarabun_BOLD(context, holder.TvNameZodiacTH, 28);
        //Font.styleText_THSarabun_BOLD(context, holder.TvNameZodiacEN, 28);
        // Font.styleText_THSarabun_Reg(context, holder.TvNameZodiacDate,28);
        val imageResourceId = context.resources.getIdentifier(
            listData[0].Data_Zodiac.zodiac_main[position].zodiac_image,
            "drawable",
            context.packageName
        )
        val bitmap: Bitmap =
            Utils.decodeSampledBitmapFromResource(context.resources, imageResourceId, 200, 200)!!
        setBackgroundV16Plus(holder!!.mImage, bitmap)

        // Utils.setStringImageDrawable(context, holder.mImage, listData.get(position).strZodiac_image, 200, 200);
        return convertView
    }

    @TargetApi(16)
    private fun setBackgroundV16Plus(view: View?, bitmap: Bitmap) {
        view!!.background = BitmapDrawable(context.resources, bitmap)
    }

    private fun setBackgroundV16Minus(view: View?, bitmap: Bitmap) {
        view!!.setBackgroundDrawable(BitmapDrawable(bitmap))
    }
}
