package de.codecentric.trakka_app.workperiod

import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import de.codecentric.trakka_app.model.Booking
import de.codecentric.trakka_app.model.BookingKind
import de.codecentric.trakka_app.model.fromDocumentSnapshot
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.properties.Delegates

typealias WorkPeriodListener = (List<Workperiod>) -> Unit
object Workperiods {
    val listeners = CopyOnWriteArrayList<WorkPeriodListener>()

    var workperiods: List<Workperiod> by Delegates.observable(mutableListOf()) { _, _, new ->
        listeners.forEach {
            Log.d("WP","Invoking callback $it")
            it.invoke(new)
        }
    }
        private set

    val updateListener = EventListener<QuerySnapshot> { value, err ->
        if (err == null) {
            val model = value!!.documents.map(::fromDocumentSnapshot)
            Log.i("WP","Received data $model")
            val newContents = constructWorkPeriods(model)
            workperiods = newContents
        }
    }

    private fun assembleWorkPeriod(root: Booking, related: List<Booking>): Workperiod? {

        val type = root.kind

        if (type != BookingKind.START && type != BookingKind.BLOCK) {
            Log.e("WP", "Workperiod malformed, root booking was $type. Root=$root, references=$related")
            return null
        }

        if (root.start == null){
            Log.e("WP","Malformed root tag, no start given")
            return null
        }

        var start: Date = root.start!!

        var end = if (type == BookingKind.START) {
            related.firstOrNull()?.takeIf { it.kind == BookingKind.END }?.end
        } else {
            root.end
        }

        val corrections = related.filter { it.kind == BookingKind.CORRECTION }
        for (c in corrections) {
            val cStart = c.start
            val cEnd = c.end

            if (cStart == null || cEnd == null) {
                Log.e("WP","Malformed correction $c, ignored")
            } else {
                start = cStart
                end  = cEnd
            }
        }


        val workperiod = Workperiod(start, end, corrections.isNotEmpty(), related)
        Log.i("WP", "Constructed WP $workperiod from ${1 + related.size} bookings")
        return workperiod
    }

    private fun constructWorkPeriods(booking: Iterable<Booking>): List<Workperiod> {
        val (roots, references) = booking.partition {
            it.reference == null
        }

        val referenceMap = references.groupBy { it.reference }

        return roots.mapNotNull { root ->
            assembleWorkPeriod(root, referenceMap[root.id] ?: emptyList())
        }
    }
}
