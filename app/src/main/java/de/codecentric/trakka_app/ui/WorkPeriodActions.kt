package de.codecentric.trakka_app.ui

import de.codecentric.trakka_app.workperiod.Workperiod
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.util.*

interface WorkPeriodActions {
    fun revoke(reference: String)
    fun correct(reference: String, start: DateTime, end: DateTime)
}