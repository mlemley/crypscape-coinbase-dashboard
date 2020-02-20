package app.lemley.crypscape.extensions.app

import android.content.Context
import android.content.SharedPreferences


val Context.sharedPreferences: SharedPreferences
    get() = this.getSharedPreferences(
        "CrypScapePreferences",
        Context.MODE_PRIVATE
    )