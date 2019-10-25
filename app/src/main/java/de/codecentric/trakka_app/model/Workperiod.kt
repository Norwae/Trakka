package de.codecentric.trakka_app.model

import org.joda.time.DateTime

data class Workperiod(val start: DateTime, val end: DateTime?,
                      val hasCorrection: Boolean,
                      val relatedBookings: List<Booking>,
                      val rootId: String)