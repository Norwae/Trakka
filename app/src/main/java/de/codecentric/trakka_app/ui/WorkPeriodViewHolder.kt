package de.codecentric.trakka_app.ui

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.codecentric.trakka_app.R
import de.codecentric.trakka_app.workperiod.Workperiod
import org.joda.time.Duration
import org.joda.time.Period
import org.joda.time.format.PeriodFormat
import java.text.DateFormat
import java.util.*

private const val TAG = "WPViewHolder"
private val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT)
private val periodFormat = PeriodFormat.wordBased()
class WorkPeriodViewHolder(itemView: View, private val actions: WorkPeriodActions) : RecyclerView.ViewHolder(itemView) {
    val duration: TextView = itemView.findViewById(R.id.duration)
    val from: TextView = itemView.findViewById(R.id.startTime)
    val to: TextView = itemView.findViewById(R.id.endTime)
    val edit: View = itemView.findViewById(R.id.edit)
    val retract: View = itemView.findViewById(R.id.delete)

    val openString = itemView.resources.getString(R.string.open_period)

    var data: Workperiod? = null
        set(value) {
            Log.i(TAG, "Set contents to $value")
            field = value
            duration.text = format(value?.start, value?.end)
            from.text = format(value?.start)
            to.text = format(value?.end)
        }

    init {

        retract.setOnClickListener {
            val data = this.data
            if (data != null) {
                Log.i(TAG, "Retracting $data")
                actions.revoke(data)
            }
        }
    }

    private fun format(start: Date?, end: Date?): String {
        return if (start != null) {
            if (end != null) {
                val period = Duration(start.time, end.time).
                    toPeriod().
                    withMillis(0).
                    withSeconds(0)

                return periodFormat.print(period)
            } else openString
        } else ""
    }

    private fun format(date: Date?): String {
        return if (date == null) "" else timeFormatter.format(date)
    }
}