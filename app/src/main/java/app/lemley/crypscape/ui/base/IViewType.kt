package app.lemley.crypscape.ui.base

import androidx.annotation.LayoutRes

interface IViewType {
    val id:Int

    @get:LayoutRes
    val layoutId:Int
}