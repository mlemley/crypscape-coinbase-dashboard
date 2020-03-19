package app.lemley.crypscape.extensions.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(
    @LayoutRes layoutId: Int,
    container: ViewGroup? = null,
    attach: Boolean = false
): View {
    return LayoutInflater.from(context).inflate(layoutId, container ?: this, attach)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}