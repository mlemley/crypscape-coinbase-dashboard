package app.lemley.crypscape.extensions.app

import java.text.DecimalFormat


fun Double.toDecimalFormat(decimalFormat: String = "#,##0.0##"): String =
    DecimalFormat(decimalFormat).format(this)