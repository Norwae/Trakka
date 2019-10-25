package de.codecentric.trakka_app.ui

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.codecentric.trakka_app.R
import de.codecentric.trakka_app.model.Bookings
import de.codecentric.trakka_app.util.periodFormater
import de.codecentric.trakka_app.util.timeFormatter
import de.codecentric.trakka_app.model.Workperiod
import org.joda.time.DateTime
import org.joda.time.Duration

private const val TAG = "WPViewHolder"
class WorkPeriodViewHolder(itemView: View, actions: WorkPeriodActions) : RecyclerView.ViewHolder(itemView) {
    private val duration: TextView = itemView.findViewById(R.id.duration)
    private val from: TextView = itemView.findViewById(R.id.startTime)
    private val to: TextView = itemView.findViewById(R.id.endTime)
    private val edit: View = itemView.findViewById(R.id.edit)
    private val retract: View = itemView.findViewById(R.id.delete)

    private val openString = itemView.resources.getString(R.string.open_period)

    var data: Workperiod? = null
        set(value) {
            Log.i(TAG, "Set contents to $value")
            field = value
            duration.text = format(value?.start, value?.end)
            from.text = format(value?.start)
            to.text = format(value?.end)

            edit.visibility = if (value?.end != null) View.VISIBLE else View.INVISIBLE
        }

    init {
        edit.setOnClickListener(makeHandler("Starting edit"){
            actions.correct(it.rootId, it.start, it.end!!)
        })
        retract.setOnClickListener(makeHandler("Starting retraction") {
            Bookings.revoke(it.rootId)
        })
    }

    private fun makeHandler(msg: String, editMethod: (Workperiod) -> Unit) = View.OnClickListener {
        val data = this.data
        if (data != null) {
            Log.i(TAG, msg)
            editMethod(data)
        }
    }

    private fun format(start: DateTime?, end: DateTime?): String {
        return if (start != null) {
            if (end != null) {
                val period = Duration(start, end).
                    toPeriod().
                    withMillis(0).
                    withSeconds(0)

                return periodFormater(period)
            } else openString
        } else ""
    }

    private fun format(date: DateTime?): String {
        return if (date == null) "" else timeFormatter.print(date)
    }
}