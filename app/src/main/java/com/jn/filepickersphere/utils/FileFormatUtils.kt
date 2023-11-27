package com.jn.filepickersphere.utils

import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

object FileFormatUtils  {

    fun formatFileSize(fileSize: Long): String {
        if (fileSize <= 0) {
            return "0 B"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(fileSize.toDouble()) / Math.log10(1024.0)).toInt()
        return (DecimalFormat("#,##0.#").format(
            fileSize / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups])
    }

    fun formatDateFromFile(path: String, isShort: Boolean): String {
        val file = File(path)
        if (!file.isFile) {
            val lastModified = file.lastModified()
            val date = Date(lastModified)
            val formattedDate = formatShortDate(date)
            val formattedDateLong = formatLongDate(date)
            return if (isShort) {
                formattedDate
            } else {
                formattedDateLong
            }
        }
        val lastModified = Date(file.lastModified())
        val formattedDateShort = formatShortDate(lastModified)
        val formattedDateLong = formatLongDate(lastModified)
        return if (isShort) {
            formattedDateShort
        } else {
            formattedDateLong
        }
    }

    private fun formatShortDate(date: Date): String {
        val dateFormat = SimpleDateFormat("d 'de' MMM", Locale.getDefault())
        return dateFormat.format(date)
    }

   private fun formatLongDate(date: Date): String {
        val dateFormat = SimpleDateFormat("d 'de' MMM. 'de' yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }
}