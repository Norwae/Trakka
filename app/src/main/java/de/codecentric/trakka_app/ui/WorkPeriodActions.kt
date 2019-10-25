package de.codecentric.trakka_app.ui

import org.joda.time.DateTime

interface WorkPeriodActions {
    fun correct(reference: String, start: DateTime, end: DateTime)
}