package com.smileapp.zodiac.imagecrop

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class CropImageView:ImageViewTouchBase {

    var mHighlightViews = ArrayList<HighlightView>()
    var mMotionHighlightView: HighlightView? = null
    var mLastX = 0f
    var mLastY:kotlin.Float = 0f
    var mMotionEdge = 0

    private var mContext: Context? = null

    override fun onLayout(
        changed: Boolean, left: Int, top: Int,
        right: Int, bottom: Int,
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (mBitmapDisplayed.getBitmap() != null) {
            for (hv in mHighlightViews) {
                hv.mMatrix!!.set(imageMatrix)
                hv.invalidate()
                if (hv.mIsFocused) {
                    centerBasedOnHighlightView(hv)
                }
            }
        }
    }

    constructor(context: Context?, attrs: AttributeSet?):super(context,attrs) {
//        super(context, attrs)
        mContext = context
    }

    override fun zoomTo(scale: Float, centerX: Float, centerY: Float) {
        super.zoomTo(scale, centerX, centerY)
        for (hv in mHighlightViews) {
            hv.mMatrix!!.set(imageMatrix)
            hv.invalidate()
        }
    }

    override fun zoomIn() {
        super.zoomIn()
        for (hv in mHighlightViews) {
            hv.mMatrix!!.set(imageMatrix)
            hv.invalidate()
        }
    }

    override fun zoomOut() {
        super.zoomOut()
        for (hv in mHighlightViews) {
            hv.mMatrix!!.set(imageMatrix)
            hv.invalidate()
        }
    }

    override fun postTranslate(deltaX: Float, deltaY: Float) {
        super.postTranslate(deltaX, deltaY)
        for (i in mHighlightViews.indices) {
            val hv = mHighlightViews[i]
            hv.mMatrix!!.postTranslate(deltaX, deltaY)
            hv.invalidate()
        }
    }

    // According to the event's position, change the focus to the first
    // hitting cropping rectangle.
    private fun recomputeFocus(event: MotionEvent) {
        for (i in mHighlightViews.indices) {
            val hv = mHighlightViews[i]
            hv.setFocus(false)
            hv.invalidate()
        }
        for (i in mHighlightViews.indices) {
            val hv = mHighlightViews[i]
            val edge = hv.getHit(event.x, event.y)
            if (edge != HighlightView().GROW_NONE) {
                if (!hv.hasFocus()) {
                    hv.setFocus(true)
                    hv.invalidate()
                }
                break
            }
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val cropImage = mContext as CropImage?
        if (cropImage!!.mSaving) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (cropImage.mWaitingToPick) {
                recomputeFocus(event)
            } else {
                var i = 0
                while (i < mHighlightViews.size) {
                    val hv = mHighlightViews[i]
                    val edge = hv.getHit(event.x, event.y)
                    if (edge != HighlightView().GROW_NONE) {
                        mMotionEdge = edge
                        mMotionHighlightView = hv
                        mLastX = event.x
                        mLastY = event.y
                        mMotionHighlightView!!.setMode(
                            if (edge == HighlightView().MOVE) HighlightView.ModifyMode.Move else HighlightView.ModifyMode.Grow
                        )
                        break
                    }
                    i++
                }
            }
            MotionEvent.ACTION_UP -> {
                if (cropImage.mWaitingToPick) {
                    var i = 0
                    while (i < mHighlightViews.size) {
                        val hv = mHighlightViews[i]
                        if (hv.hasFocus()) {
                            cropImage.mCrop = hv
                            var j = 0
                            while (j < mHighlightViews.size) {
                                if (j == i) {
                                    j++
                                    continue
                                }
                                mHighlightViews[j].setHidden(true)
                                j++
                            }
                            centerBasedOnHighlightView(hv)
                            (mContext as CropImage?)!!.mWaitingToPick = false
                            return true
                        }
                        i++
                    }
                } else if (mMotionHighlightView != null) {
                    centerBasedOnHighlightView(mMotionHighlightView!!)
                    mMotionHighlightView!!.setMode(
                        HighlightView.ModifyMode.None
                    )
                }
                mMotionHighlightView = null
            }
            MotionEvent.ACTION_MOVE -> if (cropImage.mWaitingToPick) {
                recomputeFocus(event)
            } else if (mMotionHighlightView != null) {
                mMotionHighlightView!!.handleMotion(
                    mMotionEdge,
                    event.x - mLastX,
                    event.y - mLastY
                )
                mLastX = event.x
                mLastY = event.y
                if (true) {
                    // This section of code is optional. It has some user
                    // benefit in that moving the crop rectangle against
                    // the edge of the screen causes scrolling but it means
                    // that the crop rectangle is no longer fixed under
                    // the user's finger.
                    ensureVisible(mMotionHighlightView!!)
                }
            }
        }
        when (event.action) {
            MotionEvent.ACTION_UP -> center(true, true)
            MotionEvent.ACTION_MOVE ->                 // if we're not zoomed then there's no point in even allowing
                // the user to move the image around.  This call to center puts
                // it back to the normalized location (with false meaning don't
                // animate).
                if (getScale() === 1f) {
                    center(true, true)
                }
        }
        return true
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private fun ensureVisible(hv: HighlightView) {
        val r = hv.mDrawRect
        val panDeltaX1 = max(0, mLeft - r!!.left)
        val panDeltaX2 = min(0, mRight - r.right)
        val panDeltaY1 = max(0, mTop - r.top)
        val panDeltaY2 = min(0, mBottom - r.bottom)
        val panDeltaX = if (panDeltaX1 != 0) panDeltaX1 else panDeltaX2
        val panDeltaY = if (panDeltaY1 != 0) panDeltaY1 else panDeltaY2
        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX.toFloat(), panDeltaY.toFloat())
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private fun centerBasedOnHighlightView(hv: HighlightView) {
        val drawRect = hv.mDrawRect
        val width = drawRect!!.width().toFloat()
        val height = drawRect.height().toFloat()
        val thisWidth = getWidth().toFloat()
        val thisHeight = getHeight().toFloat()
        val z1 = thisWidth / width * .6f
        val z2 = thisHeight / height * .6f
        var zoom = Math.min(z1, z2)
        zoom *= this.getScale()
        zoom = max(1f, zoom)
        if (abs(zoom - getScale()) / zoom > .1) {
            val coordinates = floatArrayOf(
                hv.mCropRect!!.centerX(),
                hv.mCropRect!!.centerY()
            )
            imageMatrix.mapPoints(coordinates)
            zoomTo(zoom, coordinates[0], coordinates[1], 300f)
        }
        ensureVisible(hv)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in mHighlightViews.indices) {
            mHighlightViews[i].draw(canvas)
        }
    }

    fun add(hv: HighlightView) {
        mHighlightViews.add(hv)
        invalidate()
    }
}