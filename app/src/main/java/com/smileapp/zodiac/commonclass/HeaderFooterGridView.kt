package com.smileapp.zodiac.commonclass

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*

/**
 * A [GridView] that supports adding header rows and footer rows in a
 * very similar way to [android.widget.ListView].
 * See [HeaderFooterGridView.addHeaderView] and
 * [HeaderFooterGridView.addFooterView].
 *
 * This source code is based from
 * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.4_r1/com/android/photos/views/HeaderGridView.java
 */
class HeaderFooterGridView : GridView {
    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    private class FixedViewInfo {
        /**
         * The view to add to the grid
         */
        var view: View? = null
        var viewContainer: ViewGroup? = null

        /**
         * The data backing the view. This is returned from [ListAdapter.getItem].
         */
        var data: Any? = null

        /**
         * `true` if the fixed view should be selectable in the grid
         */
        var isSelectable = false
    }

    private val mHeaderViewInfos = ArrayList<FixedViewInfo>()
    private val mFooterViewInfos = ArrayList<FixedViewInfo>()
    private var mRequestedNumColumns = 0
    private var mNumColmuns = 1
    private fun initHeaderGridView() {
        super.setClipChildren(false)
    }

    constructor(context: Context?) : super(context) {
        initHeaderGridView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initHeaderGridView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initHeaderGridView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mRequestedNumColumns != AUTO_FIT) {
            mNumColmuns = mRequestedNumColumns
        }
        if (mNumColmuns <= 0) {
            mNumColmuns = 1
        }
        val adapter = adapter
        if (adapter != null && adapter is HeaderFooterViewGridAdapter) {
            adapter.setNumColumns(numColumns)
        }
    }

    override fun setClipChildren(clipChildren: Boolean) {
        // Ignore, since the header rows depend on not being clipped
    }
    /**
     * Add a fixed view to appear at the top of the grid. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     *
     *
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    /**
     * Add a fixed view to appear at the top of the grid. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     *
     *
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v The view to add.
     */
    @JvmOverloads
    fun addHeaderView(v: View?, data: Any? = null, isSelectable: Boolean = true) {
        val adapter = adapter
        check(!(adapter != null && adapter !is HeaderFooterViewGridAdapter)) { "Cannot add header view to grid -- setAdapter has already been called." }
        val info = FixedViewInfo()
        val fl: FrameLayout = FullWidthFixedViewLayout(context)
        fl.addView(v)
        info.view = v
        info.viewContainer = fl
        info.data = data
        info.isSelectable = isSelectable
        mHeaderViewInfos.add(info)

        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (adapter != null) {
            (adapter as HeaderFooterViewGridAdapter).notifyDataSetChanged()
        }
    }
    /**
     * Add a fixed view to appear at the bottom of the grid. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     *
     *
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    /**
     * Add a fixed view to appear at the bottom of the grid. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     *
     *
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v The view to add.
     */
    @JvmOverloads
    fun addFooterView(v: View?, data: Any? = null, isSelectable: Boolean = true) {
        val adapter = adapter
        check(!(adapter != null && adapter !is HeaderFooterViewGridAdapter)) { "Cannot add footer view to grid -- setAdapter has already been called." }
        val info = FixedViewInfo()
        val fl: FrameLayout = FullWidthFixedViewLayout(context)
        fl.addView(v)
        info.view = v
        info.viewContainer = fl
        info.data = data
        info.isSelectable = isSelectable
        mFooterViewInfos.add(info)

        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (adapter != null) {
            (adapter as HeaderFooterViewGridAdapter).notifyDataSetChanged()
        }
    }

    val headerViewCount: Int
        get() = mHeaderViewInfos.size
    val footerViewCount: Int
        get() = mFooterViewInfos.size

    /**
     * Removes a previously-added header view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a header view
     */
    fun removeHeaderView(v: View): Boolean {
        if (mHeaderViewInfos.size > 0) {
            var result = false
            val adapter = adapter
            if (adapter != null && (adapter as HeaderFooterViewGridAdapter).removeHeader(v)) {
                result = true
            }
            removeFixedViewInfo(v, mHeaderViewInfos)
            return result
        }
        return false
    }

    /**
     * Removes a previously-added footer view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a footer view
     */
    fun removeFooterView(v: View): Boolean {
        if (mFooterViewInfos.size > 0) {
            var result = false
            val adapter = adapter
            if (adapter != null && (adapter as HeaderFooterViewGridAdapter).removeFooter(v)) {
                result = true
            }
            removeFixedViewInfo(v, mFooterViewInfos)
            return result
        }
        return false
    }

    private fun removeFixedViewInfo(v: View, where: ArrayList<FixedViewInfo>) {
        val len = where.size
        for (i in 0 until len) {
            val info = where[i]
            if (info.view === v) {
                where.removeAt(i)
                break
            }
        }
    }

    override fun setAdapter(adapter: ListAdapter) {
        if (mHeaderViewInfos.size > 0 || mFooterViewInfos.size > 0) {
            val hadapter = HeaderFooterViewGridAdapter(mHeaderViewInfos, mFooterViewInfos, adapter)
            val numColumns = numColumns
            if (numColumns > 1) {
                hadapter.setNumColumns(numColumns)
            }
            super.setAdapter(hadapter)
        } else {
            super.setAdapter(adapter)
        }
    }

    private inner class FullWidthFixedViewLayout(context: Context?) :
        FrameLayout(context!!) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var widthMeasureSpec = widthMeasureSpec
            val targetWidth = (this@HeaderFooterGridView.measuredWidth
                    - this@HeaderFooterGridView.paddingLeft
                    - this@HeaderFooterGridView.paddingRight)
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                targetWidth,
                MeasureSpec.getMode(widthMeasureSpec)
            )
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun setNumColumns(numColumns: Int) {
        super.setNumColumns(numColumns)
        // Store specified value for less than Honeycomb.
        mRequestedNumColumns = numColumns
    }

    @SuppressLint("NewApi")
    override fun getNumColumns(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.getNumColumns()
        } else mNumColmuns

        // Return value for less than Honeycomb.
    }

    /**
     * ListAdapter used when a HeaderFooterGridView has header views. This ListAdapter
     * wraps another one and also keeps track of the header views and their
     * associated data objects.
     *
     * This is intended as a base class; you will probably not need to
     * use this class directly in your own code.
     */
    private class HeaderFooterViewGridAdapter(
        headerViewInfos: ArrayList<FixedViewInfo>?,
        footerViewInfos: ArrayList<FixedViewInfo>?,
        private val mAdapter: ListAdapter,
    ) :
        WrapperListAdapter, Filterable {
        // This is used to notify the container of updates relating to number of columns
        // or headers changing, which changes the number of placeholders needed
        private val mDataSetObservable = DataSetObservable()
        private var mNumColumns = 1

        // This ArrayList is assumed to NOT be null.
        var mHeaderViewInfos: ArrayList<FixedViewInfo>
        var mFooterViewInfos: ArrayList<FixedViewInfo>
        var mAreAllFixedViewsSelectable: Boolean
        private val mIsFilterable: Boolean

        init {
            mIsFilterable = mAdapter is Filterable
            requireNotNull(headerViewInfos) { "headerViewInfos cannot be null" }
            requireNotNull(footerViewInfos) { "footerViewInfos cannot be null" }
            mHeaderViewInfos = headerViewInfos
            mFooterViewInfos = footerViewInfos
            mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(
                mFooterViewInfos
            )
        }

        val headersCount: Int
            get() = mHeaderViewInfos.size
        val footersCount: Int
            get() = mFooterViewInfos.size

        override fun isEmpty(): Boolean {
            return (mAdapter == null || mAdapter.isEmpty) && headersCount == 0 && footersCount == 0
        }

        fun setNumColumns(numColumns: Int) {
            require(numColumns >= 1) { "Number of columns must be 1 or more" }
            if (mNumColumns != numColumns) {
                mNumColumns = numColumns
                notifyDataSetChanged()
            }
        }

        private fun areAllListInfosSelectable(infos: ArrayList<FixedViewInfo>?): Boolean {
            if (infos != null) {
                for (info in infos) {
                    if (!info.isSelectable) {
                        return false
                    }
                }
            }
            return true
        }

        fun removeHeader(v: View): Boolean {
            for (i in mHeaderViewInfos.indices) {
                val info = mHeaderViewInfos[i]
                if (info.view === v) {
                    mHeaderViewInfos.removeAt(i)
                    mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(
                        mFooterViewInfos
                    )
                    mDataSetObservable.notifyChanged()
                    return true
                }
            }
            return false
        }

        fun removeFooter(v: View): Boolean {
            for (i in mFooterViewInfos.indices) {
                val info = mFooterViewInfos[i]
                if (info.view === v) {
                    mFooterViewInfos.removeAt(i)
                    mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(
                        mFooterViewInfos
                    )
                    mDataSetObservable.notifyChanged()
                    return true
                }
            }
            return false
        }

        override fun getCount(): Int {
            return if (mAdapter != null) {
                val lastRowItemCount = mAdapter.count % mNumColumns
                val emptyItemCount =
                    if (lastRowItemCount == 0) 0 else mNumColumns - lastRowItemCount
                headersCount * mNumColumns + mAdapter.count + emptyItemCount + footersCount * mNumColumns
            } else {
                headersCount * mNumColumns + footersCount * mNumColumns
            }
        }

        override fun areAllItemsEnabled(): Boolean {
            return if (mAdapter != null) {
                mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled()
            } else {
                true
            }
        }

        override fun isEnabled(position: Int): Boolean {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            val numHeadersAndPlaceholders = headersCount * mNumColumns
            if (position < numHeadersAndPlaceholders) {
                return (position % mNumColumns == 0
                        && mHeaderViewInfos[position / mNumColumns].isSelectable)
            }

            // Adapter
            if (position < numHeadersAndPlaceholders + mAdapter.count) {
                val adjPosition = position - numHeadersAndPlaceholders
                var adapterCount = 0
                if (mAdapter != null) {
                    adapterCount = mAdapter.count
                    if (adjPosition < adapterCount) {
                        return mAdapter.isEnabled(adjPosition)
                    }
                }
            }

            // Empty item
            val lastRowItemCount = mAdapter.count % mNumColumns
            val emptyItemCount = if (lastRowItemCount == 0) 0 else mNumColumns - lastRowItemCount
            if (position < numHeadersAndPlaceholders + mAdapter.count + emptyItemCount) {
                return false
            }

            // Footer
            val numFootersAndPlaceholders = footersCount * mNumColumns
            if (position < numHeadersAndPlaceholders + mAdapter.count + emptyItemCount + numFootersAndPlaceholders) {
                return (position % mNumColumns == 0
                        && mFooterViewInfos[(position - numHeadersAndPlaceholders - mAdapter.count - emptyItemCount) / mNumColumns].isSelectable)
            }
            throw ArrayIndexOutOfBoundsException(position)
        }

        override fun getItem(position: Int): Any? {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            val numHeadersAndPlaceholders = headersCount * mNumColumns
            if (position < numHeadersAndPlaceholders) {
                return if (position % mNumColumns == 0) {
                    mHeaderViewInfos[position / mNumColumns].data!!
                } else null
            }

            // Adapter
            if (position < numHeadersAndPlaceholders + mAdapter.count) {
                val adjPosition = position - numHeadersAndPlaceholders
                var adapterCount = 0
                if (mAdapter != null) {
                    adapterCount = mAdapter.count
                    if (adjPosition < adapterCount) {
                        return mAdapter.getItem(adjPosition)
                    }
                }
            }

            // Empty item
            val lastRowItemCount = mAdapter.count % mNumColumns
            val emptyItemCount = if (lastRowItemCount == 0) 0 else mNumColumns - lastRowItemCount
            if (position < numHeadersAndPlaceholders + mAdapter.count + emptyItemCount) {
                return null
            }

            // Footer
            val numFootersAndPlaceholders = footersCount * mNumColumns
            if (position < numHeadersAndPlaceholders + mAdapter.count + emptyItemCount + numFootersAndPlaceholders) {
                if (position % mNumColumns == 0) {
                    return mFooterViewInfos[(position - numHeadersAndPlaceholders - mAdapter.count - emptyItemCount) / mNumColumns].data!!
                }
            }
            throw ArrayIndexOutOfBoundsException(position)
        }

        override fun getItemId(position: Int): Long {
            val numHeadersAndPlaceholders = headersCount * mNumColumns
            if (mAdapter != null) {
                if (position >= numHeadersAndPlaceholders && position < numHeadersAndPlaceholders + mAdapter.count) {
                    val adjPosition = position - numHeadersAndPlaceholders
                    val adapterCount = mAdapter.count
                    if (adjPosition < adapterCount) {
                        return mAdapter.getItemId(adjPosition)
                    }
                }
            }
            return -1
        }

        override fun hasStableIds(): Boolean {
            return mAdapter?.hasStableIds() ?: false
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            var convertView = convertView
            val numHeadersAndPlaceholders = headersCount * mNumColumns
            if (position < numHeadersAndPlaceholders) {
                val headerViewContainer: View? =
                    mHeaderViewInfos[position / mNumColumns].viewContainer
                return if (position % mNumColumns == 0) {
                    headerViewContainer!!
                } else {
                    convertView = View(parent.context)
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.visibility = INVISIBLE
                    convertView.minimumHeight = headerViewContainer!!.height
                    convertView
                }
            }

            // Adapter
            if (position < numHeadersAndPlaceholders + mAdapter.count) {
                val adjPosition = position - numHeadersAndPlaceholders
                var adapterCount = 0
                if (mAdapter != null) {
                    adapterCount = mAdapter.count
                    if (adjPosition < adapterCount) {
                        convertView = mAdapter.getView(adjPosition, convertView, parent)
                        convertView.visibility = VISIBLE
                        return convertView
                    }
                }
            }

            // Empty item
            val lastRowItemCount = mAdapter.count % mNumColumns
            val emptyItemCount = if (lastRowItemCount == 0) 0 else mNumColumns - lastRowItemCount
            if (position < numHeadersAndPlaceholders + mAdapter.count + emptyItemCount) {
                // We need to do this because GridView uses the height of the last item
                // in a row to determine the height for the entire row.
                // TODO Current implementation may not be enough in the case of 3 or more column. May need to be careful on the INVISIBLE View height.
                convertView = mAdapter.getView(mAdapter.count - 1, convertView, parent)
                convertView.visibility = INVISIBLE
                return convertView
            }

            // Footer
            val numFootersAndPlaceholders = footersCount * mNumColumns
            if (position < numHeadersAndPlaceholders + mAdapter.count + emptyItemCount + numFootersAndPlaceholders) {
                val footerViewContainer: View? =
                    mFooterViewInfos[(position - numHeadersAndPlaceholders - mAdapter.count - emptyItemCount) / mNumColumns].viewContainer
                return if (position % mNumColumns == 0) {
                    footerViewContainer!!
                } else {
                    convertView = View(parent.context)
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.visibility = INVISIBLE
                    convertView.minimumHeight = footerViewContainer!!.height
                    convertView
                }
            }
            throw ArrayIndexOutOfBoundsException(position)
        }

        override fun getItemViewType(position: Int): Int {
            val numHeadersAndPlaceholders = headersCount * mNumColumns
            if (position < numHeadersAndPlaceholders && position % mNumColumns != 0) {
                // Placeholders get the last view type number
                return mAdapter?.viewTypeCount ?: 1
            }
            if (mAdapter != null && position >= numHeadersAndPlaceholders && position < numHeadersAndPlaceholders + mAdapter.count + (mNumColumns - mAdapter.count) % mNumColumns) {
                val adjPosition = position - numHeadersAndPlaceholders
                val adapterCount = mAdapter.count
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition)
                } else if (adapterCount != 0 && mNumColumns != 1) {
                    return mAdapter.getItemViewType(adapterCount - 1)
                }
            }
            val numFootersAndPlaceholders = footersCount * mNumColumns
            return if (mAdapter != null && position < numHeadersAndPlaceholders + mAdapter.count + numFootersAndPlaceholders) {
                mAdapter?.viewTypeCount ?: 1
            } else ITEM_VIEW_TYPE_HEADER_OR_FOOTER
        }

        override fun getViewTypeCount(): Int {
            return if (mAdapter != null) {
                mAdapter.viewTypeCount + 1
            } else 2
        }

        override fun registerDataSetObserver(observer: DataSetObserver) {
            mDataSetObservable.registerObserver(observer)
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer)
            }
        }

        override fun unregisterDataSetObserver(observer: DataSetObserver) {
            mDataSetObservable.unregisterObserver(observer)
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer)
            }
        }

        override fun getFilter(): Filter? {
            return if (mIsFilterable) {
                (mAdapter as Filterable).filter
            } else null
        }

        override fun getWrappedAdapter(): ListAdapter {
            return mAdapter
        }

        fun notifyDataSetChanged() {
            mDataSetObservable.notifyChanged()
        }
    }

    companion object {
        private const val TAG = "HeaderFooterGridView"
    }
}