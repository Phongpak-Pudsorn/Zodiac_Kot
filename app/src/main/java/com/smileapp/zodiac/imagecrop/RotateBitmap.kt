package com.smileapp.zodiac.imagecrop

import android.graphics.Bitmap
import android.graphics.Matrix

class RotateBitmap {

    val TAG = "RotateBitmap"
    private var mBitmap: Bitmap? = null
    private var mRotation = 0

    constructor(bitmap: Bitmap?) {
        mBitmap = bitmap
        mRotation = 0
    }

    constructor(bitmap: Bitmap?, rotation: Int) {
        mBitmap = bitmap
        mRotation = rotation % 360
    }

    fun setRotation(rotation: Int) {
        mRotation = rotation
    }

    fun getRotation(): Int {
        return mRotation
    }

    fun getBitmap(): Bitmap? {
        return mBitmap
    }

    fun setBitmap(bitmap: Bitmap?) {
        mBitmap = bitmap
    }

    fun getRotateMatrix(): Matrix {
        // By default this is an identity matrix.
        val matrix = Matrix()
        if (mRotation != 0) {
            // We want to do the rotation at origin, but since the bounding
            // rectangle will be changed after rotation, so the delta values
            // are based on old & new width/height respectively.
            val cx = mBitmap!!.width / 2
            val cy = mBitmap!!.height / 2
            matrix.preTranslate(-cx.toFloat(), -cy.toFloat())
            matrix.postRotate(mRotation.toFloat())
            matrix.postTranslate((getWidth() / 2).toFloat(), (getHeight() / 2).toFloat())
        }
        return matrix
    }

    fun isOrientationChanged(): Boolean {
        return mRotation / 90 % 2 != 0
    }

    fun getHeight(): Int {
        return if (isOrientationChanged()) {
            mBitmap!!.width
        } else {
            mBitmap!!.height
        }
    }

    fun getWidth(): Int {
        return if (isOrientationChanged()) {
            mBitmap!!.height
        } else {
            mBitmap!!.width
        }
    }

    fun recycle() {
        if (mBitmap != null) {
            mBitmap!!.recycle()
            mBitmap = null
        }
    }
}