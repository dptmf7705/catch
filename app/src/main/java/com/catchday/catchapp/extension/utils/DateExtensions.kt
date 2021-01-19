package com.catchday.catchapp.extension.utils

import java.text.SimpleDateFormat
import java.util.*

val currentDateTimeString: String
    get() = Date().getDateTimeString()

fun Date.getDateTimeString() =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(this).toString()
