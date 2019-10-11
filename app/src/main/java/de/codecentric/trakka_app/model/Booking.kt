package de.codecentric.trakka_app.model

import java.util.*

data class Booking(
    var kind: BookingKind = BookingKind.START,
    val start: Date? = null,
    val end: Date? = null,
    val timestamp: Date = Date(),
    val company: String = ""
)