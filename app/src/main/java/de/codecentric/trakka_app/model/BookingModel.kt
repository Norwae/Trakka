package de.codecentric.trakka_app.model

import android.app.Activity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

interface BookingSetChangedListener : EventListener {
    fun onBookingSetChanged(bookings: List<Booking>)
}

class BookingModel(
    firebase: FirebaseFirestore,
    private val owner: Activity,
    userId: String
): CompanyChangeListener {
    private val bookings = firebase.collection("users/${userId}/bookings")
    private var registration: ListenerRegistration? = null
    private var currentCompany: CompanyMembership = NoCompany

    val listeners = CopyOnWriteArraySet<BookingSetChangedListener>()

    override fun onCompanyChanged(company: CompanyMembership) {
        currentCompany = company
        registration?.remove()
        registration = bookings// .whereEqualTo("company", company.id)
            .addSnapshotListener(owner) { bookings, err ->
                if (err == null && bookings != null) {
                    val set = bookings.map { snap ->
                        snap.toObject(Booking::class.java).also {booking ->
                            booking.id = snap.id
                        }
                    }

                    for (l in listeners){
                        l.onBookingSetChanged(set)
                    }
                }
            }
    }

    fun block(start: DateTime, end: DateTime){
        bookings.add(Booking(BookingKind.BLOCK, company = currentCompany.id, start = start.toDate(), end = end.toDate()))
    }

    fun correct(reference: String, start: DateTime, end: DateTime) {
        bookings.add(Booking(BookingKind.CORRECTION, company = currentCompany.id, start = start.toDate(), end = end.toDate(), reference = reference))
    }

    fun revoke(reference: String){
        bookings.add(Booking(BookingKind.RETRACTION, company = currentCompany.id, reference = reference))
    }

    fun start() {
        bookings.add(Booking(BookingKind.START, company = currentCompany.id, start = DateTime.now().toDate()))
    }

    fun end(reference: String) {
        bookings.add(Booking(BookingKind.END, company = currentCompany.id, end = DateTime.now().toDate(), reference = reference))
    }
}