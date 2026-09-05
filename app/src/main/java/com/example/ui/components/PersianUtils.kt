package com.example.ui.components

import java.text.DecimalFormat

fun String.toPersianDigits(): String {
    return this.map { char ->
        when (char) {
            '0' -> '۰'
            '1' -> '۱'
            '2' -> '۲'
            '3' -> '۳'
            '4' -> '۴'
            '5' -> '۵'
            '6' -> '۶'
            '7' -> '۷'
            '8' -> '۸'
            '9' -> '۹'
            else -> char
        }
    }.joinToString("")
}

fun Long.toPersianFormatted(): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(this).toPersianDigits()
}

fun Int.toPersianFormatted(): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(this).toPersianDigits()
}

fun Double.toPersianFormatted(decimals: Int = 1): String {
    val pattern = if (decimals <= 0) "#,###" else "#,###." + "#".repeat(decimals)
    val formatter = DecimalFormat(pattern)
    return formatter.format(this).toPersianDigits()
}
