@file:JvmName("Metrics")

package com.wsoteam.diet.utils

import android.content.Context
import android.util.TypedValue
import java.text.DecimalFormat
import java.util.*

fun dpF(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics)
}

fun dp(context: Context, dp: Float): Int {
    return dpF(context, dp).toInt()
}


val currency: Currency
    get() {
        return if (Locale.getDefault().language.equals("ru")) {
            Currency.getInstance("RUB")
        } else {
            Currency.getInstance("USD")
        }
    }

val currencyFormatter = DecimalFormat().apply {
    currency = currency
}

val Number.asCurrency: String
    get() = "$this${currency.symbol}"


fun Context.dp2px(value: Float): Int {
    return dp(this, value)
}

fun Context.dp2pxF(value: Float): Float {
    return dpF(this, value)
}