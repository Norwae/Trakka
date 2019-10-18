package de.codecentric.trakka_app.util

import org.joda.time.format.PeriodFormat
import java.text.DateFormat

val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT)
val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT)
val periodFormat = PeriodFormat.wordBased()
