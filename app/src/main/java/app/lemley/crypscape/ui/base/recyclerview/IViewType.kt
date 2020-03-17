package app.lemley.crypscape.ui.base.recyclerview

import androidx.annotation.LayoutRes

interface IViewType {
    val id:Int

    @get:LayoutRes
    val layoutId:Int
}