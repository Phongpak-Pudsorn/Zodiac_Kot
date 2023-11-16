package com.smileapp.zodiac.commonclass

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

//public class CMTextView extends TextView {
//	public static Typeface mBoldFont;
//	public static Typeface mNormalFont;
//
//	public CMTextView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//
//	}
//
//	public CMTextView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//
//	}
//
//	public CMTextView(Context context) {
//		super(context);
//
//	}
//
//	public void setTypeface(Typeface tf, int style) {
//		if (mBoldFont == null) {
//			mBoldFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/psl030pro.ttf");
//		}
//		if (mNormalFont == null) {
//			mNormalFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/psl030pro.ttf");
//
//		}
//		if (style == Typeface.BOLD) {
//			super.setTypeface(mBoldFont);
//		} else {
//			super.setTypeface(mNormalFont);
//		}
//	}
//}
class CMTextView : AppCompatTextView {
    constructor(context: Context?) : super(context!!) {
        setFont()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        setFont()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        setFont()
    }

    private fun setFont() {
        val font = Typeface.createFromAsset(context.assets, "fonts/psl030pro.ttf")
        setTypeface(font, Typeface.NORMAL)
    }
}