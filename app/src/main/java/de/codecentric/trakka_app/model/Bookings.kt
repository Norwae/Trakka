package de.codecentric.trakka_app.model

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

object Bookings {
    val bookingListeners = CopyOnWriteArraySet<UpdateListener<List<Booking>>>()
    var bookings by UpdateDispatcher(emptyList(), bookingListeners)
        private set
    lateinit var bookingsCollection: CollectionReference
    private var listener: ListenerRegistration? = null

    fun revoke(reference: String) {
        bookingsCollection.add(Booking(kind = BookingKind.RETRACTION, company = CompanySelector.currentCompany.id, reference = reference))
    }

    fun start() {
        bookingsCollection.add(Booking(kind = BookingKind.START, start = Date(), company = CompanySelector.currentCompany.id))
    }

    fun end(reference: String) {
        bookingsCollection.add(Booking(kind = BookingKind.END, end = Date(), company = CompanySelector.currentCompany.id, reference = reference))
    }

    fun block(start: DateTime, end: DateTime) {
        bookingsCollection.add(Booking(kind = BookingKind.END, start = start.toDate(), end = end.toDate(), company = CompanySelector.currentCompany.id))
    }

    fun correct(reference: String, start: DateTime, end: DateTime) {
        bookingsCollection.add(Booking(kind = BookingKind.CORRECTION, start = start.toDate(), end = end.toDate(), company = CompanySelector.currentCompany.id, reference = reference))
    }


    fun init(userId: String) {
        bookingsCollection = firestore.collection("/users/$userId/bookings")
        CompanySelector.companyChangeListeners += object : UpdateListener<CompanyMembership> {
            override fun onUpdated(oldValue: CompanyMembership, newValue: CompanyMembership) {
                listener?.remove()
                listener = bookingsCollection.whereEqualTo("company", newValue.id)
                    .addSnapshotListener { snapshot, e ->
                        if (e == null && snapshot != null) {
                            bookings = snapshot.documents.map(Booking.Companion::fromDocumentSnapshot)
                        } else {
                            Log.e(TAG, "Bookings collection listener reported error", e)
                        }
                    }
            }
        }
    }
}