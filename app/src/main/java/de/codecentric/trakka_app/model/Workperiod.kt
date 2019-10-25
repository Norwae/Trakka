package de.codecentric.trakka_app.model

import de.codecentric.trakka_app.model.Booking
import org.joda.time.DateTime
import java.util.*

data class Workperiod(val start: DateTime, val end: DateTime?,
                      val hasCorrection: Boolean,
                      val relatedBookings: List<Booking>,
                      val rootId: String)