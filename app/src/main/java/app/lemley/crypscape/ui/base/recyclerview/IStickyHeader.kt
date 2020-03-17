package app.lemley.crypscape.ui.base.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

interface IStickyHeader {

    /**
     * This method gets called by {@link StickHeaderItemDecoration} to fetch the position
     * of the header item in the adapter that is used for (represents) item at specified position.
     *
     * @param itemPosition int. Adapter's position of the item for which
     *                     to do the search of the position of the header item.
     *
     * @return int. Position of the header item in the adapter.
     */
    fun headerPositionForItem(itemPosition: Int): Int

    /**
     * This method gets called by {@link StickHeaderItemDecoration} to get layout resource
     * id for the header item at specified adapter's position.
     *
     * @param headerPosition int. Position of the header item in the adapter.
     *
     * @return int. Layout resource id.
     */
    fun headerLayout(headerPosition: Int): Int

    /**
     * This method gets called by {@link StickHeaderItemDecoration} to setup the header View.
     *
     * @param header View. Header to set the data on.
     *
     * @param headerPosition int. Position of the header item in the adapter.
     */
    fun bindHeaderData(parent: ViewGroup, header: View, headerPosition: Int)

    /**
     * This method gets called by {@link StickHeaderItemDecoration} to verify
     * whether the item represents a header.
     *
     * @param itemPosition:Int.
     *
     * @return true, if item at the specified adapter's position represents a header.
     */
    fun isHeader(itemPosition:Int):Boolean
}