package app.lemley.crypscape.extensions.app

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes


fun <T : View> Activity.withView(@IdRes viewId: Int): T = this.findViewById<T>(viewId)
