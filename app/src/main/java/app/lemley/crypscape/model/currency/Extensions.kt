package app.lemley.crypscape.model.currency

import com.crypscape.currencies.UsdCurrency

fun Float.toUsd(): UsdCurrency = this.toDouble().toUsd()
fun Double.toUsd(): UsdCurrency = UsdCurrency(this)
fun Long.toUsd(): UsdCurrency = UsdCurrency(this)
fun Int.toUsd(): UsdCurrency = this.toLong().toUsd()

