package app.lemley.crypscape.extensions.app

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

fun <T : View> Fragment.withView(@IdRes viewId: Int): T? = this.view?.findViewById<T?>(viewId)
