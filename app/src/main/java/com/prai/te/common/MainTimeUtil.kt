package com.prai.te.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

internal object MainTimeUtil {
    fun isoToFullCustom(isoString: String): String {
        val zonedDateTime = ZonedDateTime.parse(isoString)
        val localDateTime = zonedDateTime.toLocalDateTime()
        val dayOfWeek = localDateTime.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.SHORT,
            Locale.KOREAN
        )

        val amPm = if (localDateTime.hour < 12) "오전" else "오후"
        val hour = if (localDateTime.hour % 12 == 0) 12 else localDateTime.hour % 12

        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val datePart = localDateTime.format(formatter)
        val timePart = String.format("%02d:%02d", hour, localDateTime.minute)

        return "$datePart ($dayOfWeek) $amPm $timePart"
    }

    fun isoToDateCustom(isoString: String): String {
        val zonedDateTime = ZonedDateTime.parse(isoString)
        val localDateTime = zonedDateTime.toLocalDateTime()
        val dayOfWeek = localDateTime.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.SHORT,
            Locale.KOREAN
        )

        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val datePart = localDateTime.format(formatter)

        return "$datePart ($dayOfWeek)"
    }

    fun isoToTimeCustom(isoString: String): String {
        val zonedDateTime = ZonedDateTime.parse(isoString)
        val localDateTime = zonedDateTime.toLocalDateTime()

        val amPm = if (localDateTime.hour < 12) "오전" else "오후"
        val hour = if (localDateTime.hour % 12 == 0) 12 else localDateTime.hour % 12

        val timePart = String.format("%02d:%02d", hour, localDateTime.minute)

        return "$amPm $timePart"
    }

    @Composable
    fun secondToMinuteString(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        return if (minutes == 0)  {
            String.format(
                LocalConfiguration.current.locales.get(0),
                "%d초",
                remainingSeconds
            )
        } else {
            String.format(
                LocalConfiguration.current.locales.get(0),
                "%d분 %d초",
                minutes,
                remainingSeconds
            )
        }
    }

    @Composable
    fun brithMillsToString(time: Long?): String {
        if (time == null) {
            return "NO DATA"
        }
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return dateFormat.format(Date(time))
    }
}