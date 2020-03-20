package app.lemley.crypscape.extensions.app

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration


val Context.sharedPreferences: SharedPreferences
    get() = this.getSharedPreferences(
        "CrypScapePreferences",
        Context.MODE_PRIVATE
    )

fun Context.isLandscape(): Boolean =
    this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
