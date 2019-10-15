package de.codecentric.trakka_app.ui

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import de.codecentric.trakka_app.R
import de.codecentric.trakka_app.workperiod.Workperiod
import java.text.DateFormat
import java.util.*

private const val TAG = "WPViewHolder"
private val formatter = DateFormat.getTimeInstance(DateFormat.SHORT)

class WorkPeriodViewHolder(itemView: View, private val actions: WorkPeriodActions) : RecyclerView.ViewHolder(itemView) {
    val from: EditText = itemView.findViewById(R.id.from)
    val to: EditText = itemView.findViewById(R.id.to)
    val edit: Button = itemView.findViewById(R.id.edit)
    val retract: Button = itemView.findViewById(R.id.retract)

    var data: Workperiod? = null
        set(value) {
            field = value
            from.setText(format(value?.start))
            to.setText(format(value?.end))
        }

    init {
        edit.setOnClickListener {
            Log.i(TAG, "Oops no edit")
        }

        retract.setOnClickListener {
            val data = this.data
            if (data != null) {
                Log.i(TAG, "Retracting $data")
                actions.revoke(data)
            }
        }
    }

    private fun format(date: Date?): String {
        return if (date == null) "" else formatter.format(date)
    }
}