package co.tddl.mylga.util

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun getTodaysDate(): String{

    if(Build.VERSION.SDK_INT >= 26){
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        return current.format(formatter)
    }

    val date = Date()
    val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
    return formatter.format(date)
}