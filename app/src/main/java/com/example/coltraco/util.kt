package com.example.coltraco

import java.text.SimpleDateFormat
import java.util.*

fun calculateAge(day: Int, month: Int, year: Int): Int {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val birthDateString = "$day/$month/$year"
    val birthDate = dateFormatter.parse(birthDateString)
    val currentDate = Date()
    val diffInMillis = currentDate.time - birthDate.time
    val ageInMillis = Calendar.getInstance().apply {
        timeInMillis = diffInMillis
    }.get(Calendar.YEAR) - 1970
    return ageInMillis.toInt()
}
fun isDateValid(day: Int, month: Int, year: Int): Boolean {
    val daysInMonth = when(month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> return false
    }

    return day in 1..daysInMonth
}

fun isDateBeforeToday(day: Int, month: Int, year: Int): Boolean {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currentDate = sdf.parse(sdf.format(Date()))
    val inputDate = sdf.parse("$day/$month/$year")

    return inputDate.before(currentDate)
}