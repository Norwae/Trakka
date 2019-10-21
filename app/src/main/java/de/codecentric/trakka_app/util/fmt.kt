package de.codecentric.trakka_app.util

import org.joda.time.Period
import java.text.DateFormat

val timeFormatter: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
private const val fmt = "%02d:%02d"

fun periodFormater(period: Period) = fmt.format(period.hours, period.minutes)
