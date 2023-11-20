package com.smileapp.zodiac.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.smileapp.zodiac.R
import com.smileapp.zodiac.utils.Utils


class SpinnerAdapter(context:Context, textViewId:Int, list:ArrayList<String>):ArrayAdapter<String>(context,textViewId,list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        (v as TextView).setBackgroundColor(Color.TRANSPARENT)
        com.smileapp.zodiac.commonclass.Font().styleText_RSU_BOLD(context,v,24,"#FFFFFF")
        return v
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getDropDownView(position, convertView, parent)
        if (position==Utils.getRasi()){
            v.setBackgroundColor(Color.rgb(56, 184, 226))
        }else{
            v.setBackgroundResource(R.drawable.click_spinner)
        }
        return v
    }
}