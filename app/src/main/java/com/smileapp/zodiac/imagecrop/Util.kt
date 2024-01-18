package com.smileapp.zodiac.imagecrop

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.*
import android.os.Handler
import android.view.Surface
import java.io.Closeable

object Util {

    private val TAG = "db.Util"


    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that are tolerable
     * in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     */


    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that are tolerable
     * in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     */
    fun transform(
        scaler: Matrix?,
        source: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        scaleUp: Boolean,
    ): Bitmap? {
        var scaler = scaler
        val deltaX = source.width - targetWidth
        val deltaY = source.height - targetHeight
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
             * than the target.  Transform it by placing as much of the image
             * as possible into the target and leaving the top/bottom or
             * left/right (or both) black.
             */
            val b2 = Bitmap.createBitmap(
                targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888
            )
            val c = Canvas(b2)
            val deltaXHalf = Math.max(0, deltaX / 2)
            val deltaYHalf = Math.max(0, deltaY / 2)
            val src = Rect(
                deltaXHalf,
                deltaYHalf,
                deltaXHalf + Math.min(targetWidth, source.width),
                deltaYHalf + Math.min(targetHeight, source.height)
            )
            val dstX = (targetWidth - src.width()) / 2
            val dstY = (targetHeight - src.height()) / 2
            val dst = Rect(
                dstX,
                dstY,
                targetWidth - dstX,
                targetHeight - dstY
            )
            c.drawBitmap(source, src, dst, null)
            return b2
        }
        val bitmapWidthF = source.width.toFloat()
        val bitmapHeightF = source.height.toFloat()
        val bitmapAspect = bitmapWidthF / bitmapHeightF
        val viewAspect = targetWidth.toFloat() / targetHeight
        if (bitmapAspect > viewAspect) {
            val scale = targetHeight / bitmapHeightF
            if (scale < .9f || scale > 1f) {
                scaler!!.setScale(scale, scale)
            } else {
                scaler = null
            }
        } else {
            val scale = targetWidth / bitmapWidthF
            if (scale < .9f || scale > 1f) {
                scaler!!.setScale(scale, scale)
            } else {
                scaler = null
            }
        }
        val b1: Bitmap
        b1 = if (scaler != null) {
            // this is used for minithumb and crop, so we want to mFilter here.
            Bitmap.createBitmap(
                source, 0, 0,
                source.width, source.height, scaler, true
            )
        } else {
            source
        }
        val dx1 = Math.max(0, b1.width - targetWidth)
        val dy1 = Math.max(0, b1.height - targetHeight)
        val b2 = Bitmap.createBitmap(
            b1,
            dx1 / 2,
            dy1 / 2,
            targetWidth,
            targetHeight
        )
        if (b1 != source) {
            b1.recycle()
        }
        return b2
    }

    fun closeSilently(c: Closeable?) {
        if (c == null) return
        try {
            c.close()
        } catch (t: Throwable) {
            // do nothing
        }
    }

    private class BackgroundJob(
        private val mActivity: MonitoredActivity,
        private val mJob: Runnable,
        private val mDialog: ProgressDialog,
        handler: Handler,
    ) :
        MonitoredActivity.LifeCycleAdapter(), Runnable {
        private val mHandler: Handler
        private val mCleanupRunner = Runnable {
            mActivity.removeLifeCycleListener(this@BackgroundJob)
            if (mDialog.window != null) mDialog.dismiss()
        }

        init {
            mActivity.addLifeCycleListener(this)
            mHandler = handler
        }

        override fun run() {
            try {
                mJob.run()
            } finally {
                mHandler.post(mCleanupRunner)
            }
        }

        override fun onActivityDestroyed(activity: MonitoredActivity?) {
            // We get here only when the onDestroyed being called before
            // the mCleanupRunner. So, run it now and remove it from the queue
            mCleanupRunner.run()
            mHandler.removeCallbacks(mCleanupRunner)
        }

        override fun onActivityStopped(activity: MonitoredActivity?) {
            mDialog.hide()
        }

        override fun onActivityStarted(activity: MonitoredActivity?) {
            mDialog.show()
        }
    }

    fun startBackgroundJob(
        activity: MonitoredActivity,
        title: String?, message: String?, job: Runnable, handler: Handler,
    ) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        val dialog = ProgressDialog.show(
            activity, title, message, true, false
        )
        Thread(BackgroundJob(activity, job, dialog, handler)).start()
    }


    // Returns Options that set the puregeable flag for Bitmap decode.
    fun createNativeAllocOptions(): BitmapFactory.Options? {
        //options.inNativeAlloc = true;
        return BitmapFactory.Options()
    }

    // Thong added for rotate
    fun rotateImage(src: Bitmap, degree: Float): Bitmap? {
        // create new matrix
        val matrix = Matrix()
        // setup rotation degree
        matrix.postRotate(degree)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    @SuppressLint("NewApi")
    fun getOrientationInDegree(activity: Activity): Int {
        val rotation = activity.windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        return degrees
    }
}