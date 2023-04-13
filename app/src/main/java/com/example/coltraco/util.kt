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