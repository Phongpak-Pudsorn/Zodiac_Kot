package com.smileapp.zodiac.commonclass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import com.smileapp.zodiac.R

class MultiDirectionSlidingDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0,
) :
    ViewGroup(context, attrs, defStyle) {
    private val mHandleId: Int
    private val mContentId: Int

    /**
     * Returns the handle of the drawer.
     *
     * @return The View reprenseting the handle of the drawer, identified by the
     * "handle" id in XML.
     */
    var handle: View? = null
        private set

    /**
     * Returns the content of the drawer.
     *
     * @return The View reprenseting the content of the drawer, identified by the
     * "content" id in XML.
     */
    var content: View? = null
        private set
    private val mFrame = Rect()
    private val mInvalidate = Rect()
    private var mTracking = false
    private var mLocked = false
    private var mVelocityTracker: VelocityTracker? = null
    private val mInvert: Boolean
    private val mVertical: Boolean

    /**
     * Indicates whether the drawer is currently fully opened.
     *
     * @return True if the drawer is opened, false otherwise.
     */
    var isOpened = false
        private set
    private val mBottomOffset: Int
    private val mTopOffset: Int
    private var mHandleHeight = 0
    private var mHandleWidth = 0
    private var mOnDrawerOpenListener: OnDrawerOpenListener? = null
    private var mOnDrawerCloseListener: OnDrawerCloseListener? = null
    private var mOnDrawerScrollListener: OnDrawerScrollListener? = null
    private val mHandler: Handler = SlidingHandler()
    private var mAnimatedAcceleration = 0f
    private var mAnimatedVelocity = 0f
    private var mAnimationPosition = 0f
    private var mAnimationLastTime: Long = 0
    private var mCurrentAnimationTime: Long = 0
    private var mTouchDelta = 0
    private var mAnimating = false
    private val mAllowSingleTap: Boolean
    private val mAnimateOnClick: Boolean
    private val mTapThreshold: Int
    private val mMaximumTapVelocity: Int
    private var mMaximumMinorVelocity: Int
    private var mMaximumMajorVelocity: Int
    private var mMaximumAcceleration: Int
    private val mVelocityUnits: Int

    /**
     * Callback invoked when the drawer is opened.
     */
    interface OnDrawerOpenListener {
        /**
         * Invoked when the drawer becomes fully open.
         */
        fun onDrawerOpened()
    }

    /**
     * Callback invoked when the drawer is closed.
     */
    interface OnDrawerCloseListener {
        /**
         * Invoked when the drawer becomes fully closed.
         */
        fun onDrawerClosed()
    }

    /**
     * Callback invoked when the drawer is scrolled.
     */
    interface OnDrawerScrollListener {
        /**
         * Invoked when the user starts dragging/flinging the drawer's handle.
         */
        fun onScrollStarted()

        /**
         * Invoked when the user stops dragging/flinging the drawer's handle.
         */
        fun onScrollEnded()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        handle = findViewById(mHandleId)
        if (handle == null) {
            throw IllegalArgumentException("The handle attribute is must refer to an" + " existing child.")
        }
        handle!!.setOnClickListener(DrawerToggler())
        content = findViewById(mContentId)
        if (content == null) {
            throw IllegalArgumentException(
                "The content attribute is must refer to an"
                        + " existing child."
            )
        }
        content!!.visibility = GONE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            throw RuntimeException(
                "SlidingDrawer cannot have UNSPECIFIED dimensions"
            )
        }
        val handle = handle
        measureChild(handle, widthMeasureSpec, heightMeasureSpec)
        if (mVertical) {
            val height = heightSpecSize - handle!!.measuredHeight - mTopOffset
            content!!.measure(
                MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        } else {
            val width = widthSpecSize - handle!!.measuredWidth - mTopOffset
            content!!.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY)
            )
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val drawingTime = drawingTime
        val handle = handle
        val isVertical = mVertical
        drawChild(canvas, handle, drawingTime)
        if (mTracking || mAnimating) {
            val cache = content!!.drawingCache
            if (cache != null) {
                if (isVertical) {
                    if (mInvert) {
                        canvas.drawBitmap(
                            cache,
                            0f,
                            (handle!!.top - (bottom - top) + mHandleHeight).toFloat(),
                            null
                        )
                    } else {
                        canvas.drawBitmap(cache, 0f, handle!!.bottom.toFloat(), null)
                    }
                } else {
                    if (mInvert){
                        canvas.drawBitmap(cache,(handle!!.left - cache.width).toFloat(),0f,null)
                    }else {
                        canvas.drawBitmap(cache,handle!!.right.toFloat(),0f,null)
                    }
                }
            } else {
                canvas.save()
                if (mInvert) {
                    if (isVertical){
                        canvas.translate(0f,(handle!!.top - mTopOffset - content!!.measuredHeight).toFloat())
                    }else{
                        canvas.translate((handle!!.left - mTopOffset - content!!.measuredWidth).toFloat(),0f)
                    }
                } else {
                    if (isVertical){
                        canvas.translate(0f,(handle!!.left - mTopOffset.toFloat()).toFloat())
                    }else{
                        canvas.translate((handle!!.top - mTopOffset).toFloat(),0f)
                    }
                }
                drawChild(canvas, content, drawingTime)
                canvas.restore()
            }
            invalidate()
        } else if (isOpened) {
            drawChild(canvas, content, drawingTime)
        }
    }
    /**
     * Creates a new SlidingDrawer from a specified set of attributes defined in
     * XML.
     *
     * @param context
     * The application's environment.
     * @param attrs
     * The attributes defined in XML.
     * @param defStyle
     * The style to apply to this widget.
     */
    /**
     * Creates a new SlidingDrawer from a specified set of attributes defined in
     * XML.
     *
     * @param context
     * The application's environment.
     * @param attrs
     * The attributes defined in XML.
     */
    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.MultiDirectionSlidingDrawer,
            defStyle,
            0
        )
        val orientation =
            a.getInt(R.styleable.MultiDirectionSlidingDrawer_direction, ORIENTATION_BTT)
        mVertical = (orientation == ORIENTATION_BTT || orientation == ORIENTATION_TTB)
        mBottomOffset =
            a.getDimension(R.styleable.MultiDirectionSlidingDrawer_bottomOffset, 0.0f).toInt()
        mTopOffset = a.getDimension(R.styleable.MultiDirectionSlidingDrawer_topOffset, 0.0f).toInt()
        mAllowSingleTap = a.getBoolean(R.styleable.MultiDirectionSlidingDrawer_allowSingleTap, true)
        mAnimateOnClick = a.getBoolean(R.styleable.MultiDirectionSlidingDrawer_animateOnClick, true)
        mInvert = (orientation == ORIENTATION_TTB || orientation == ORIENTATION_LTR)
        val handleId = a.getResourceId(R.styleable.MultiDirectionSlidingDrawer_handle, 0)
        if (handleId == 0) {
            throw IllegalArgumentException(
                ("The handle attribute is required and must refer "
                        + "to a valid child.")
            )
        }
        val contentId = a.getResourceId(R.styleable.MultiDirectionSlidingDrawer_content, 0)
        if (contentId == 0) {
            throw IllegalArgumentException(
                ("The content attribute is required and must refer "
                        + "to a valid child.")
            )
        }
        if (handleId == contentId) {
            throw IllegalArgumentException(
                ("The content and handle attributes must refer "
                        + "to different children.")
            )
        }
        mHandleId = handleId
        mContentId = contentId
        val density = resources.displayMetrics.density
        mTapThreshold = (TAP_THRESHOLD * density + 0.5f).toInt()
        mMaximumTapVelocity = (MAXIMUM_TAP_VELOCITY * density + 0.5f).toInt()
        mMaximumMinorVelocity = (MAXIMUM_MINOR_VELOCITY * density + 0.5f).toInt()
        mMaximumMajorVelocity = (MAXIMUM_MAJOR_VELOCITY * density + 0.5f).toInt()
        mMaximumAcceleration = (MAXIMUM_ACCELERATION * density + 0.5f).toInt()
        mVelocityUnits = (VELOCITY_UNITS * density + 0.5f).toInt()
        if (mInvert) {
            mMaximumAcceleration = -mMaximumAcceleration
            mMaximumMajorVelocity = -mMaximumMajorVelocity
            mMaximumMinorVelocity = -mMaximumMinorVelocity
        }
        a.recycle()
        isAlwaysDrawnWithCacheEnabled = false
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mTracking) {
            return
        }
        val width = r - l
        val height = b - t
        val handle = handle
        val handleWidth = handle!!.measuredWidth
        val handleHeight = handle.measuredHeight
        Log.d(
            LOG_TAG,
            "handleHeight: $handleHeight"
        )
        val handleLeft: Int
        val handleTop: Int
        val content = content
        if (mVertical) {
            handleLeft = (width - handleWidth) / 2
            if (mInvert) {
                Log.d(LOG_TAG, "content.layout(1)")
                handleTop = if (isOpened) height - mBottomOffset - handleHeight else mTopOffset
                content!!.layout(
                    0,
                    mTopOffset,
                    content.measuredWidth,
                    mTopOffset + content.measuredHeight
                )
            } else {
                handleTop = if (isOpened) mTopOffset else height - handleHeight + mBottomOffset
                content!!.layout(
                    0,
                    mTopOffset + handleHeight,
                    content.measuredWidth,
                    mTopOffset + handleHeight + content.measuredHeight
                )
            }
        } else {
            handleTop = (height - handleHeight) / 2
            if (mInvert) {
                handleLeft = if (isOpened) width - mBottomOffset - handleWidth else mTopOffset
                content!!.layout(
                    mTopOffset,
                    0,
                    mTopOffset + content.measuredWidth,
                    content.measuredHeight
                )
            } else {
                handleLeft = if (isOpened) mTopOffset else width - handleWidth + mBottomOffset
                content!!.layout(
                    mTopOffset + handleWidth,
                    0,
                    mTopOffset + handleWidth + content.measuredWidth,
                    content.measuredHeight
                )
            }
        }
        handle.layout(handleLeft, handleTop, handleLeft + handleWidth, handleTop + handleHeight)
        mHandleHeight = handle.height
        mHandleWidth = handle.width
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (mLocked) {
            return false
        }
        val action = event.action
        val x = event.x
        val y = event.y
        val frame = mFrame
        val handle = handle
        handle!!.getHitRect(frame)
        if (!mTracking && !frame.contains(x.toInt(), y.toInt())) {
            return false
        }
        if (action == MotionEvent.ACTION_DOWN) {
            mTracking = true
            handle.isPressed = true
            // Must be called before prepareTracking()
            prepareContent()

            // Must be called after prepareContent()
            if (mOnDrawerScrollListener != null) {
                mOnDrawerScrollListener!!.onScrollStarted()
            }
            if (mVertical) {
                val top = handle.top
                mTouchDelta = y.toInt() - top
                prepareTracking(top)
            } else {
                val left = handle.left
                mTouchDelta = x.toInt() - left
                prepareTracking(left)
            }
            mVelocityTracker!!.addMovement(event)
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mLocked) {
            return true
        }
        if (mTracking) {
            mVelocityTracker!!.addMovement(event)
            val action = event.action
            when (action) {
                MotionEvent.ACTION_MOVE -> moveHandle((if (mVertical) event.y else event.x).toInt() - mTouchDelta)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val velocityTracker = mVelocityTracker
                    velocityTracker!!.computeCurrentVelocity(mVelocityUnits)
                    var yVelocity = velocityTracker.yVelocity
                    var xVelocity = velocityTracker.xVelocity
                    val negative: Boolean
                    val vertical = mVertical
                    if (vertical) {
                        negative = yVelocity < 0
                        if (xVelocity < 0) {
                            xVelocity = -xVelocity
                        }
                        // fix by Maciej Ciemięga.
                        if ((!mInvert && xVelocity > mMaximumMinorVelocity) || (mInvert && xVelocity < mMaximumMinorVelocity)) {
                            xVelocity = mMaximumMinorVelocity.toFloat()
                        }
                    } else {
                        negative = xVelocity < 0
                        if (yVelocity < 0) {
                            yVelocity = -yVelocity
                        }
                        // fix by Maciej Ciemięga.
                        if ((!mInvert && yVelocity > mMaximumMinorVelocity) || (mInvert && yVelocity < mMaximumMinorVelocity)) {
                            yVelocity = mMaximumMinorVelocity.toFloat()
                        }
                    }
                    var velocity = Math.hypot(xVelocity.toDouble(), yVelocity.toDouble()).toFloat()
                    if (negative) {
                        velocity = -velocity
                    }
                    val handleTop = handle!!.top
                    val handleLeft = handle!!.left
                    val handleBottom = handle!!.bottom
                    val handleRight = handle!!.right
                    if (Math.abs(velocity) < mMaximumTapVelocity) {
                        val c1: Boolean
                        val c2: Boolean
                        val c3: Boolean
                        val c4: Boolean
                        if (mInvert) {
                            c1 =
                                (isOpened && (bottom - handleBottom) < mTapThreshold + mBottomOffset)
                            c2 =
                                (!isOpened && handleTop < mTopOffset + mHandleHeight - mTapThreshold)
                            c3 = (isOpened && (right - handleRight) < mTapThreshold + mBottomOffset)
                            c4 =
                                (!isOpened && handleLeft > mTopOffset + mHandleWidth + mTapThreshold)
                        } else {
                            c1 = (isOpened && handleTop < mTapThreshold + mTopOffset)
                            c2 =
                                (!isOpened && handleTop > (mBottomOffset + bottom) - top - mHandleHeight - mTapThreshold)
                            c3 = (isOpened && handleLeft < mTapThreshold + mTopOffset)
                            c4 =
                                (!isOpened && handleLeft > (mBottomOffset + right) - left - mHandleWidth - mTapThreshold)
                        }
                        Log.d(
                            LOG_TAG,
                            "ACTION_UP: c1: $c1, c2: $c2, c3: $c3, c4: $c4"
                        )
                        if (if (vertical) c1 || c2 else c3 || c4) {
                            if (mAllowSingleTap) {
                                playSoundEffect(SoundEffectConstants.CLICK)
                                if (isOpened) {
                                    animateClose(if (vertical) handleTop else handleLeft)
                                } else {
                                    animateOpen(if (vertical) handleTop else handleLeft)
                                }
                            } else {
                                performFling(
                                    if (vertical) handleTop else handleLeft,
                                    velocity,
                                    false
                                )
                            }
                        } else {
                            performFling(if (vertical) handleTop else handleLeft, velocity, false)
                        }
                    } else {
                        performFling(if (vertical) handleTop else handleLeft, velocity, false)
                    }
                }
            }
        }
        return mTracking || mAnimating || super.onTouchEvent(event)
    }

    private fun animateClose(position: Int) {
        prepareTracking(position)
        performFling(position, mMaximumAcceleration.toFloat(), true)
    }

    private fun animateOpen(position: Int) {
        prepareTracking(position)
        performFling(position, -mMaximumAcceleration.toFloat(), true)
    }

    private fun performFling(position: Int, velocity: Float, always: Boolean) {
        mAnimationPosition = position.toFloat()
        mAnimatedVelocity = velocity
        val c1: Boolean
        val c2: Boolean
        val c3: Boolean
        if (isOpened) {
            val bottom = if (mVertical) bottom else right
            val handleHeight = if (mVertical) mHandleHeight else mHandleWidth
            Log.d(
                LOG_TAG,
                "position: $position, velocity: $velocity, mMaximumMajorVelocity: $mMaximumMajorVelocity"
            )
            c1 = if (mInvert) velocity < mMaximumMajorVelocity else velocity > mMaximumMajorVelocity
            c2 =
                if (mInvert) (bottom - (position + handleHeight)) + mBottomOffset > handleHeight else position > mTopOffset + (if (mVertical) mHandleHeight else mHandleWidth)
            c3 =
                if (mInvert) velocity < -mMaximumMajorVelocity else velocity > -mMaximumMajorVelocity
            Log.d(
                LOG_TAG,
                "EXPANDED. c1: $c1, c2: $c2, c3: $c3"
            )
            if (always || (c1 || (c2 && c3))) {
                // We are expanded, So animate to CLOSE!
                mAnimatedAcceleration = mMaximumAcceleration.toFloat()
                if (mInvert) {
                    if (velocity > 0) {
                        mAnimatedVelocity = 0f
                    }
                } else {
                    if (velocity < 0) {
                        mAnimatedVelocity = 0f
                    }
                }
            } else {
                // We are expanded, but they didn't move sufficiently to cause
                // us to retract. Animate back to the expanded position. so animate BACK to expanded!
                mAnimatedAcceleration = -mMaximumAcceleration.toFloat()
                if (mInvert) {
                    if (velocity < 0) {
                        mAnimatedVelocity = 0f
                    }
                } else {
                    if (velocity > 0) {
                        mAnimatedVelocity = 0f
                    }
                }
            }
        } else {

            // WE'RE COLLAPSED
            c1 = if (mInvert) velocity < mMaximumMajorVelocity else velocity > mMaximumMajorVelocity
            c2 =
                if (mInvert) (position < (if (mVertical) height else width) / 2) else (position > (if (mVertical) height else width) / 2)
            c3 =
                if (mInvert) velocity < -mMaximumMajorVelocity else velocity > -mMaximumMajorVelocity
            Log.d(
                LOG_TAG,
                "COLLAPSED. position: $position, velocity: $velocity, mMaximumMajorVelocity: $mMaximumMajorVelocity"
            )
            Log.d(
                LOG_TAG,
                "COLLAPSED. always: $always, c1: $c1, c2: $c2, c3: $c3"
            )
            if (!always && (c1 || (c2 && c3))) {
                mAnimatedAcceleration = mMaximumAcceleration.toFloat()
                if (mInvert) {
                    if (velocity > 0) {
                        mAnimatedVelocity = 0f
                    }
                } else {
                    if (velocity < 0) {
                        mAnimatedVelocity = 0f
                    }
                }
            } else {
                mAnimatedAcceleration = -mMaximumAcceleration.toFloat()
                if (mInvert) {
                    if (velocity < 0) {
                        mAnimatedVelocity = 0f
                    }
                } else {
                    if (velocity > 0) {
                        mAnimatedVelocity = 0f
                    }
                }
            }
        }
        val now = SystemClock.uptimeMillis()
        mAnimationLastTime = now
        mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION
        mAnimating = true
        mHandler.removeMessages(MSG_ANIMATE)
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime)
        stopTracking()
    }

    private fun prepareTracking(position: Int) {
        mTracking = true
        mVelocityTracker = VelocityTracker.obtain()
        val opening = !isOpened
        if (opening) {
            mAnimatedAcceleration = mMaximumAcceleration.toFloat()
            mAnimatedVelocity = mMaximumMajorVelocity.toFloat()
            if (mInvert) mAnimationPosition = mTopOffset.toFloat() else mAnimationPosition =
                (mBottomOffset + (if (mVertical) height - mHandleHeight else width - mHandleWidth)).toFloat()
            moveHandle(mAnimationPosition.toInt())
            mAnimating = true
            mHandler.removeMessages(MSG_ANIMATE)
            val now = SystemClock.uptimeMillis()
            mAnimationLastTime = now
            mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION
            mAnimating = true
        } else {
            if (mAnimating) {
                mAnimating = false
                mHandler.removeMessages(MSG_ANIMATE)
            }
            moveHandle(position)
        }
    }

    private fun moveHandle(position: Int) {
        val handle = handle
        if (mVertical) {
            if (position == EXPANDED_FULL_OPEN) {
                if (mInvert) handle!!.offsetTopAndBottom((mBottomOffset + bottom) - top - mHandleHeight) else handle!!.offsetTopAndBottom(
                    mTopOffset - handle.top
                )
                invalidate()
            } else if (position == COLLAPSED_FULL_CLOSED) {
                if (mInvert) {
                    handle!!.offsetTopAndBottom(mTopOffset - handle.top)
                } else {
                    handle!!.offsetTopAndBottom((mBottomOffset + bottom) - top - mHandleHeight - handle.top)
                }
                invalidate()
            } else {
                val top = handle!!.top
                var deltaY = position - top
                if (position < mTopOffset) {
                    deltaY = mTopOffset - top
                } else if (deltaY > (mBottomOffset + bottom) - getTop() - mHandleHeight - top) {
                    deltaY = (mBottomOffset + bottom) - getTop() - mHandleHeight - top
                }
                handle.offsetTopAndBottom(deltaY)
                val frame = mFrame
                val region = mInvalidate
                handle.getHitRect(frame)
                region.set(frame)
                region.union(frame.left, frame.top - deltaY, frame.right, frame.bottom - deltaY)
                region.union(
                    0,
                    frame.bottom - deltaY,
                    width,
                    frame.bottom - deltaY + content!!.height
                )
                invalidate(region)
            }
        } else {
            if (position == EXPANDED_FULL_OPEN) {
                if (mInvert) handle!!.offsetLeftAndRight((mBottomOffset + right) - left - mHandleWidth) else handle!!.offsetLeftAndRight(
                    mTopOffset - handle.left
                )
                invalidate()
            } else if (position == COLLAPSED_FULL_CLOSED) {
                if (mInvert) handle!!.offsetLeftAndRight(mTopOffset - handle.left) else handle!!.offsetLeftAndRight(
                    (mBottomOffset + right) - left - mHandleWidth - handle.left
                )
                invalidate()
            } else {
                val left = handle!!.left
                var deltaX = position - left
                if (position < mTopOffset) {
                    deltaX = mTopOffset - left
                } else if (deltaX > (mBottomOffset + right) - getLeft() - mHandleWidth - left) {
                    deltaX = (mBottomOffset + right) - getLeft() - mHandleWidth - left
                }
                handle.offsetLeftAndRight(deltaX)
                val frame = mFrame
                val region = mInvalidate
                handle.getHitRect(frame)
                region.set(frame)
                region.union(frame.left - deltaX, frame.top, frame.right - deltaX, frame.bottom)
                region.union(
                    frame.right - deltaX, 0, frame.right - deltaX + content!!.width,
                    height
                )
                invalidate(region)
            }
        }
    }

    private fun prepareContent() {
        if (mAnimating) {
            return
        }

        // Something changed in the content, we need to honor the layout request
        // before creating the cached bitmap
        val content = content
        if (content!!.isLayoutRequested) {
            if (mVertical) {
                val handleHeight = mHandleHeight
                val height = bottom - top - handleHeight - mTopOffset
                content.measure(
                    MeasureSpec.makeMeasureSpec(right - left, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                )
                Log.d(LOG_TAG, "content.layout(2)")
                if (mInvert) content.layout(
                    0,
                    mTopOffset,
                    content.measuredWidth,
                    mTopOffset + content.measuredHeight
                ) else content.layout(
                    0,
                    mTopOffset + handleHeight,
                    content.measuredWidth,
                    mTopOffset + handleHeight + content.measuredHeight
                )
            } else {
                val handleWidth = handle!!.width
                val width = right - left - handleWidth - mTopOffset
                content.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(
                        bottom - top, MeasureSpec.EXACTLY
                    )
                )
                if (mInvert) content.layout(
                    mTopOffset,
                    0,
                    mTopOffset + content.measuredWidth,
                    content.measuredHeight
                ) else content.layout(
                    handleWidth + mTopOffset,
                    0,
                    mTopOffset + handleWidth + content.measuredWidth,
                    content.measuredHeight
                )
            }
        }
        // Try only once... we should really loop but it's not a big deal
        // if the draw was cancelled, it will only be temporary anyway
        content.viewTreeObserver.dispatchOnPreDraw()
        content.buildDrawingCache()
        content.visibility = GONE
    }

    private fun stopTracking() {
        handle!!.isPressed = false
        mTracking = false
        if (mOnDrawerScrollListener != null) {
            mOnDrawerScrollListener!!.onScrollEnded()
        }
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun doAnimation() {
        if (mAnimating) {
            incrementAnimation()
            if (mInvert) {
                if (mAnimationPosition < mTopOffset) {
                    mAnimating = false
                    closeDrawer()
                } else if (mAnimationPosition >= mTopOffset + (if (mVertical) height else width) - 1) {
                    mAnimating = false
                    openDrawer()
                } else {
                    moveHandle(mAnimationPosition.toInt())
                    mCurrentAnimationTime += ANIMATION_FRAME_DURATION.toLong()
                    mHandler.sendMessageAtTime(
                        mHandler.obtainMessage(MSG_ANIMATE),
                        mCurrentAnimationTime
                    )
                }
            } else {
                if (mAnimationPosition >= mBottomOffset + (if (mVertical) height else width) - 1) {
                    mAnimating = false
                    closeDrawer()
                } else if (mAnimationPosition < mTopOffset) {
                    mAnimating = false
                    openDrawer()
                } else {
                    moveHandle(mAnimationPosition.toInt())
                    mCurrentAnimationTime += ANIMATION_FRAME_DURATION.toLong()
                    mHandler.sendMessageAtTime(
                        mHandler.obtainMessage(MSG_ANIMATE),
                        mCurrentAnimationTime
                    )
                }
            }
        }
    }

    private fun incrementAnimation() {
        val now = SystemClock.uptimeMillis()
        val t = (now - mAnimationLastTime) / 1000.0f // ms -> s
        val position = mAnimationPosition
        val v = mAnimatedVelocity // px/s
        val a = if (mInvert) mAnimatedAcceleration else mAnimatedAcceleration // px/s/s
        mAnimationPosition = position + (v * t) + (0.5f * a * t * t) // px
        mAnimatedVelocity = v + (a * t) // px/s
        mAnimationLastTime = now // ms
    }

    /**
     * Toggles the drawer open and close. Takes effect immediately.
     *
     * @see .open
     * @see .close
     * @see .animateClose
     * @see .animateOpen
     * @see .animateToggle
     */
    fun toggle() {
        if (!isOpened) {
            openDrawer()
        } else {
            closeDrawer()
        }
        invalidate()
        requestLayout()
    }

    /**
     * Toggles the drawer open and close with an animation.
     *
     * @see .open
     * @see .close
     * @see .animateClose
     * @see .animateOpen
     * @see .toggle
     */
    fun animateToggle() {
        if (!isOpened) {
            animateOpen()
        } else {
            animateClose()
        }
    }

    /**
     * Opens the drawer immediately.
     *
     * @see .toggle
     * @see .close
     * @see .animateOpen
     */
    fun open() {
        openDrawer()
        invalidate()
        requestLayout()
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
    }

    /**
     * Closes the drawer immediately.
     *
     * @see .toggle
     * @see .open
     * @see .animateClose
     */
    fun close() {
        closeDrawer()
        invalidate()
        requestLayout()
    }

    /**
     * Closes the drawer with an animation.
     *
     * @see .close
     * @see .open
     * @see .animateOpen
     * @see .animateToggle
     * @see .toggle
     */
    fun animateClose() {
        prepareContent()
        val scrollListener = mOnDrawerScrollListener
        scrollListener?.onScrollStarted()
        animateClose(if (mVertical) handle!!.top else handle!!.left)
        scrollListener?.onScrollEnded()
    }

    /**
     * Opens the drawer with an animation.
     *
     * @see .close
     * @see .open
     * @see .animateClose
     * @see .animateToggle
     * @see .toggle
     */
    fun animateOpen() {
        prepareContent()
        val scrollListener = mOnDrawerScrollListener
        scrollListener?.onScrollStarted()
        animateOpen(if (mVertical) handle!!.top else handle!!.left)
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        scrollListener?.onScrollEnded()
    }

    private fun closeDrawer() {
        moveHandle(COLLAPSED_FULL_CLOSED)
        content!!.visibility = GONE
        content!!.destroyDrawingCache()
        if (!isOpened) {
            return
        }
        isOpened = false
        if (mOnDrawerCloseListener != null) {
            mOnDrawerCloseListener!!.onDrawerClosed()
        }
    }

    private fun openDrawer() {
        moveHandle(EXPANDED_FULL_OPEN)
        content!!.visibility = VISIBLE
        if (isOpened) {
            return
        }
        isOpened = true
        if (mOnDrawerOpenListener != null) {
            mOnDrawerOpenListener!!.onDrawerOpened()
        }
    }

    /**
     * Sets the listener that receives a notification when the drawer becomes
     * open.
     *
     * @param onDrawerOpenListener
     * The listener to be notified when the drawer is opened.
     */
    fun setOnDrawerOpenListener(onDrawerOpenListener: OnDrawerOpenListener?) {
        mOnDrawerOpenListener = onDrawerOpenListener
    }

    /**
     * Sets the listener that receives a notification when the drawer becomes
     * close.
     *
     * @param onDrawerCloseListener
     * The listener to be notified when the drawer is closed.
     */
    fun setOnDrawerCloseListener(onDrawerCloseListener: OnDrawerCloseListener?) {
        mOnDrawerCloseListener = onDrawerCloseListener
    }

    /**
     * Sets the listener that receives a notification when the drawer starts or
     * ends a scroll. A fling is considered as a scroll. A fling will also
     * trigger a drawer opened or drawer closed event.
     *
     * @param onDrawerScrollListener
     * The listener to be notified when scrolling starts or stops.
     */
    fun setOnDrawerScrollListener(onDrawerScrollListener: OnDrawerScrollListener?) {
        mOnDrawerScrollListener = onDrawerScrollListener
    }

    /**
     * Unlocks the SlidingDrawer so that touch events are processed.
     *
     * @see .lock
     */
    fun unlock() {
        mLocked = false
    }

    /**
     * Locks the SlidingDrawer so that touch events are ignores.
     *
     * @see .unlock
     */
    fun lock() {
        mLocked = true
    }

    /**
     * Indicates whether the drawer is scrolling or flinging.
     *
     * @return True if the drawer is scroller or flinging, false otherwise.
     */
    val isMoving: Boolean
        get() = mTracking || mAnimating

    private inner class DrawerToggler() : OnClickListener {
        override fun onClick(v: View) {
            if (mLocked) {
                return
            }
            // mAllowSingleTap isn't relevant here; you're *always*
            // allowed to open/close the drawer by clicking with the
            // trackball.
            if (mAnimateOnClick) {
                animateToggle()
            } else {
                toggle()
            }
        }
    }

    private inner class SlidingHandler() : Handler() {
        override fun handleMessage(m: Message) {
            when (m.what) {
                MSG_ANIMATE -> doAnimation()
            }
        }
    }

    companion object {
        val ORIENTATION_RTL = 0
        val ORIENTATION_BTT = 1
        val ORIENTATION_LTR = 2
        val ORIENTATION_TTB = 3
        private val TAP_THRESHOLD = 6
        private val MAXIMUM_TAP_VELOCITY = 100.0f
        private val MAXIMUM_MINOR_VELOCITY = 150.0f
        private val MAXIMUM_MAJOR_VELOCITY = 200.0f
        private val MAXIMUM_ACCELERATION = 2000.0f
        private val VELOCITY_UNITS = 1000
        private val MSG_ANIMATE = 1000
        private val ANIMATION_FRAME_DURATION = 1000 / 60
        private val EXPANDED_FULL_OPEN = -10001
        private val COLLAPSED_FULL_CLOSED = -10002
        val LOG_TAG = "Sliding"
    }
}
