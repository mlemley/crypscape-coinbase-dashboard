package app.lemley.crypscape.ui.base.recyclerview

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.lemley.crypscape.extensions.app.inflate

class StickyHeaderDecoration constructor(
    private val stickyHeader: IStickyHeader
) : RecyclerView.ItemDecoration() {

    // Todo Request bind of header
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        parent.getChildAt(0)?.let { topChild ->
            val topChildPosition = parent.getChildLayoutPosition(topChild)

            if (topChildPosition == RecyclerView.NO_POSITION) return

            val headerPosition = stickyHeader.headerPositionForItem(topChildPosition)
            val currentHeader = parent.inflate(stickyHeader.headerLayout(headerPosition))
            stickyHeader.bindHeaderData(parent, currentHeader, headerPosition)
            fixLayout(parent, currentHeader)
            val contactPoint = currentHeader.bottom
            childInContact(parent, contactPoint, headerPosition, currentHeader)?.let {
                if (stickyHeader.isHeader(parent.getChildAdapterPosition(it))) {
                    moveHeader(c, currentHeader, it)
                    return
                }
            }

            drawHeader(c, currentHeader)
        }
    }

    private fun drawHeader(c: Canvas, currentHeader: View) {
        c.save()
        c.translate(0F, 0F)
        currentHeader.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0F, (nextHeader.top - nextHeader.height).toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    private fun childInContact(
        parent: RecyclerView,
        contactPoint: Int,
        headerPosition: Int,
        currentHeader: View
    ): View? = run loop@{
        (0 until parent.childCount).forEach {
            val child = parent.getChildAt(it)

            // measure height tolerance with child if child is another header
            val heightTolerance = if (headerPosition != it)
                if (stickyHeader.isHeader(parent.getChildAdapterPosition(child)))
                    currentHeader.layoutParams.height - child.height
                else 0
            else 0

            // add heightTolerance if child top within display area
            val childBottomPosition = if (child.top > 0)
                child.bottom + heightTolerance
            else child.bottom

            if (childBottomPosition > contactPoint) {
                if (child.top < contactPoint) {
                    return@loop child
                }
            }
        }
        null
    }

    private fun fixLayout(parent: RecyclerView, currentHeader: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            currentHeader.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            parent.height,
            parent.paddingTop + parent.paddingBottom,
            currentHeader.layoutParams.height
        )

        currentHeader.measure(childWidthSpec, childHeightSpec)

        currentHeader.layout(0, 0, currentHeader.measuredWidth, currentHeader.measuredHeight)
    }
}