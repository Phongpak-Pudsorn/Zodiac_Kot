package com.smileapp.zodiac.commonclass

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView

class Font {


    fun styleText_RSU_Reg(context: Context, textView: TextView, size: Int): TextView? {
        val type = Typeface.createFromAsset(context.assets, "fonts/psl029pro.ttf")
        textView.typeface = type
        textView.textSize = size.toFloat()
        return textView
    }

    fun styleText_RSU_Reg(
        context: Context,
        textView: TextView,
        size: Int,
        textColor: String?,
    ): TextView? {
        // textView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        textView.setTextColor(Color.parseColor(textColor))
        val type = Typeface.createFromAsset(context.assets, "fonts/psl029pro.ttf")
        textView.typeface = type
        textView.textSize = size.toFloat()
        return textView
    }


    fun styleText_RSU_BOLD(context: Context, textView: TextView, size: Int): TextView? {
        // textView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        //textView.setTextColor(Color.parseColor(textColor));
        val type = Typeface.createFromAsset(context.assets, "fonts/psl030pro.ttf")
        textView.typeface = type
        textView.textSize = size.toFloat()
        return textView
    }

    fun styleText_RSU_BOLD(
        context: Context,
        textView: TextView,
        size: Int,
        textColor: String?,
    ): TextView? {
        // textView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        textView.setTextColor(Color.parseColor(textColor))
        val type = Typeface.createFromAsset(context.assets, "fonts/psl030pro.ttf")
        textView.typeface = type
        textView.textSize = size.toFloat()
        return textView
    }

    fun styleText_THSarabun_BOLD(context: Context, textView: TextView, size: Int): TextView? {
        // textView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        //textView.setTextColor(Color.parseColor(textColor));
        val type = Typeface.createFromAsset(context.assets, "fonts/THSarabunNew Bold.ttf")
        textView.typeface = type
        textView.textSize = size.toFloat()
        return textView
    }


    fun styleText_THSarabun_Reg(context: Context, textView: TextView, size: Int): TextView? {
        // textView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        //textView.setTextColor(Color.parseColor(textColor));
        val type = Typeface.createFromAsset(context.assets, "fonts/THSarabunNew.ttf")
        textView.typeface = type
        textView.textSize = size.toFloat()
        return textView
    }


}