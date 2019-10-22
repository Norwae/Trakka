package de.codecentric.trakka_app.util

import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import java.text.DateFormat

val timeFormatter = DateTimeFormat.shortTime()
private const val fmt = "%02d:%02d"

fun periodFormater(period: Period) = fmt.format(period.hours, period.minutes)
