package de.codecentric.trakka_app.ui

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.codecentric.trakka_app.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class TextualCalendarFragment : Fragment(R.layout.textual_calendar) {
    private val format = DateTimeFormat.shortDate()
    private val dateText
        get() = view?.findViewById<EditText>(R.id.text_calendar_date)
    private val button
        get() = view?.findViewById<View>(R.id.text_calendar_calendar)

    var currentValue: LocalDate?
        get() = dateText?.text?.let {
            runCatching {
                format.parseLocalDate(it.toString())
            }.getOrNull()
        }
        set(value) {
            dateText?.setText(format.print(value), TextView.BufferType.EDITABLE)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button?.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            button?.setOnClickListener {
                val current = currentValue ?: LocalDate.now()
                val dialog = DatePickerDialog(requireContext())
                dialog.datePicker.updateDate(current.year, current.monthOfYear, current.dayOfMonth)
                dialog.setOnDateSetListener { _, year, month, day ->
                    currentValue = LocalDate(year, month, day)
                }

                dialog.show()
            }
        } else {
            button?.visibility = View.GONE
        }
    }
}