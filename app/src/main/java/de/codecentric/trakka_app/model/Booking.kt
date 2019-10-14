package de.codecentric.trakka_app.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.util.*

fun fromDocumentSnapshot(ds: DocumentSnapshot): Booking {
    return ds.toObject(Booking::class.java)!!.also {
        it.id = ds.id
    }
}

data class Booking(
    var kind: BookingKind = BookingKind.START,
    var start: Date? = null,
    var end: Date? = null,
    var timestamp: Date = Date(),
    var company: String = "",
    var reference: String? = null,
    @get:Exclude var id: String? = null
)
