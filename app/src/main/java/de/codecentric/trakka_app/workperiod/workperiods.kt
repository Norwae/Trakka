package de.codecentric.trakka_app.workperiod

import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import de.codecentric.trakka_app.model.Booking
import de.codecentric.trakka_app.model.BookingKind
import de.codecentric.trakka_app.model.fromDocumentSnapshot
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.properties.Delegates

const val BUILD_WORK_PERIOD_TAG = "WorkPeriod"
typealias WorkPeriodListener = (List<Workperiod>) -> Unit

object Workperiods {
    val listeners = CopyOnWriteArrayList<WorkPeriodListener>()

    var workperiods: List<Workperiod> by Delegates.observable(mutableListOf()) { _, _, new ->
        for (listener in listeners) {
            Log.d(
                BUILD_WORK_PERIOD_TAG,
                "Invoking callback ${listener::class} with ${new.size} records"
            )
            listener.invoke(new)
        }
    }
        private set

    val updateListener = EventListener<QuerySnapshot> { value, err ->
        if (err == null) {
            val model = value!!.documents.map(::fromDocumentSnapshot)
            Log.i("WP", "Received data (${model.size} records)")
            val newContents = constructWorkPeriods(model).sortedByDescending { it.start }
            workperiods = newContents
        } else {
            Log.e(BUILD_WORK_PERIOD_TAG, "Update had error marker", err)
        }
    }

    private fun assembleWorkPeriod(root: Booking, related: List<Booking>): Workperiod? {
        val type = root.kind
        val id = root.id


        if (id == null) {
            Log.e(BUILD_WORK_PERIOD_TAG, "Cannot build a workperiod out of an unsaved booking")
            return null
        }

        if (type != BookingKind.START && type != BookingKind.BLOCK) {
            Log.e(
                BUILD_WORK_PERIOD_TAG,
                "Workperiod malformed, root booking was $type. Root=$root, references=$related"
            )
            return null
        }

        var start = root.start
        var end = root.end
        var corrected = false

        for (rel in related) {
            when (rel.kind) {
                BookingKind.END ->
                    if (end == null && rel.end != null) {
                        end = rel.end
                    } else {
                        Log.e(
                            BUILD_WORK_PERIOD_TAG,
                            "Unusuable end booking encountered (start=$start, end=$end, tag=$rel)"
                        )
                    }
                BookingKind.CORRECTION ->
                    if (rel.start == null || rel.end == null) {
                        corrected = true
                        start = rel.start
                        end = rel.end
                    } else {
                        Log.e(
                            BUILD_WORK_PERIOD_TAG,
                            "Unusable correction booking encountered (start=$start, end=$end, tag=$rel)"
                        )
                    }
                BookingKind.RETRACTION -> {
                    Log.d(BUILD_WORK_PERIOD_TAG, "Retraction record encountered: $rel")
                    return null
                }
                else ->
                    Log.d(
                        BUILD_WORK_PERIOD_TAG,
                        "Related booking of type ${rel.kind} was encountered that is not an expected related type: $rel"
                    )
            }
        }

        return if (start != null) {
            Workperiod(start, end, corrected, related + root, id).also {
                Log.i(
                    BUILD_WORK_PERIOD_TAG,
                    "Constructed WP $it from ${1 + related.size} bookings"
                )
            }
        } else {
            Log.e(BUILD_WORK_PERIOD_TAG, "Could not build work period, no start set")
            null
        }
    }

    private fun constructWorkPeriods(booking: Iterable<Booking>): List<Workperiod> {
        val (roots, references) = booking.partition(Booking::isRoot)
        val referenceMap = references.groupBy(Booking::reference)

        return roots.mapNotNull { root ->
            assembleWorkPeriod(root, referenceMap[root.id] ?: emptyList())
        }
    }
}
