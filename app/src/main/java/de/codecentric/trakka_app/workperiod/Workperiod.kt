package de.codecentric.trakka_app.workperiod

import de.codecentric.trakka_app.model.Booking
import java.util.*

data class Workperiod(val start: Date, val end: Date?, val hasCorrection: Boolean, val relatedBookings: List<Booking>, val rootId: String)