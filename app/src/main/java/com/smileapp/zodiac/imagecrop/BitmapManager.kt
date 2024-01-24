package com.smileapp.zodiac.imagecrop

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.FileDescriptor
import java.util.*

class BitmapManager {

    private val TAG = "BitmapManager"

    private enum class State {
        CANCEL, ALLOW
    }

    private class ThreadStatus {
        var mState = State.ALLOW
        var mOptions: BitmapFactory.Options? = null
        override fun toString(): String {
            var s: String
            s = if (mState == State.CANCEL) {
                "Cancel"
            } else if (mState == State.ALLOW) {
                "Allow"
            } else {
                "?"
            }
            s = "thread state = $s, options = $mOptions"
            return s
        }
    }

    class ThreadSet : Iterable<Thread?> {
        private val mWeakCollection = WeakHashMap<Thread, Any?>()
        fun add(t: Thread) {
            mWeakCollection[t] = null
        }

        fun remove(t: Thread) {
            mWeakCollection.remove(t)
        }

        override fun iterator(): Iterator<Thread> {
            return mWeakCollection.keys.iterator()
        }
    }

    private val mThreadStatus = WeakHashMap<Thread, ThreadStatus>()

    private var sManager: BitmapManager? = null

    constructor() {}

    /**
     * Get thread status and create one if specified.
     */
    @Synchronized
    private fun getOrCreateThreadStatus(t: Thread): ThreadStatus {
        var status = mThreadStatus[t]
        if (status == null) {
            status = ThreadStatus()
            mThreadStatus[t] = status
        }
        return status
    }

    /**
     * The following three methods are used to keep track of
     * BitmapFaction.Options used for decoding and cancelling.
     */
    @Synchronized
    private fun setDecodingOptions(
        t: Thread,
        options: BitmapFactory.Options,
    ) {
        getOrCreateThreadStatus(t).mOptions = options
    }

    @Synchronized
    fun getDecodingOptions(t: Thread): BitmapFactory.Options? {
        val status = mThreadStatus[t]
        return status?.mOptions
    }

    @Synchronized
    fun removeDecodingOptions(t: Thread) {
        val status = mThreadStatus[t]
        status!!.mOptions = null
    }

    /**
     * The following two methods are used to allow/cancel a set of threads
     * for bitmap decoding.
     */
    @Synchronized
    fun allowThreadDecoding(threads: ThreadSet) {
        for (t in threads) {
            allowThreadDecoding(t)
        }
    }

    @Synchronized
    fun cancelThreadDecoding(threads: ThreadSet) {
        for (t in threads) {
            cancelThreadDecoding(t)
        }
    }

    /**
     * The following three methods are used to keep track of which thread
     * is being disabled for bitmap decoding.
     */
    @Synchronized
    fun canThreadDecoding(t: Thread): Boolean {
        val status = mThreadStatus[t]
            ?: // allow decoding by default
            return true
        return status.mState != State.CANCEL
    }

    @Synchronized
    fun allowThreadDecoding(t: Thread) {
        getOrCreateThreadStatus(t).mState = State.ALLOW
    }

    @Synchronized
    fun cancelThreadDecoding(t: Thread) {
        val status = getOrCreateThreadStatus(t)
        status.mState = State.CANCEL
        if (status.mOptions != null) {
            status.mOptions!!.requestCancelDecode()
        }

        // Wake up threads in waiting list
//        notifyAll()
    }

    /**
     * A debugging routine.
     */
    @Synchronized
    fun dump() {
        val i: Iterator<Map.Entry<Thread, ThreadStatus>> = mThreadStatus.entries.iterator()
        while (i.hasNext()) {
            val (key, value) = i.next()
            Log.v(
                TAG, "[Dump] Thread " + key + " ("
                        + key.id
                        + ")'s status is " + value
            )
        }
    }

    @Synchronized
    fun instance(): BitmapManager? {
        if (sManager == null) {
            sManager = BitmapManager()
        }
        return sManager
    }

    /**
     * The real place to delegate bitmap decoding to BitmapFactory.
     */
//    fun decodeFileDescriptor(
//        fd: FileDescriptor?,
//        options: BitmapFactory.Options,
//    ): Bitmap? {
//        if (options.mCancel) {
//            return null
//        }
//        val thread = Thread.currentThread()
//        if (!canThreadDecoding(thread)) {
//            // Log.d(TAG, "Thread " + thread + " is not allowed to decode.");
//            return null
//        }
//        setDecodingOptions(thread, options)
//        val b = BitmapFactory.decodeFileDescriptor(fd, null, options)
//        removeDecodingOptions(thread)
//        return b
//    }
}