package de.codecentric.trakka_app

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import de.codecentric.trakka_app.ui.WorkPeriodCorrection
import de.codecentric.trakka_app.util.dateFormatter
import de.codecentric.trakka_app.util.timeFormatter
import de.codecentric.trakka_app.workperiod.Workperiod
import kotlinx.android.synthetic.main.edit_period.*
import org.joda.time.LocalDateTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

const val editedWorkPeriodKey = "EditedWorkPeriod"

class EditPeriodActivity : AppCompatActivity() {
    private lateinit var dateView: DatePicker
    private lateinit var startView: TimePicker
    private lateinit var endView: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_period)

        val editedWorkPeriod = intent.extras!![editedWorkPeriodKey] as WorkPeriodCorrection

        dateView = findViewById(R.id.date)
        startView = findViewById(R.id.startTime)
        endView = findViewById(R.id.endTime)

        startView.setIs24HourView(true)
        endView.setIs24HourView(true)
        dateView.calendarViewShown = false

        findViewById<View>(R.id.doIt).setOnClickListener{
            setResult(Activity.RESULT_OK, Intent().apply {
                val start = LocalDateTime(dateView.year, dateView.month, dateView.dayOfMonth,
                    startView.hour, startView.minute)
                val end = LocalDateTime(dateView.year, dateView.month, dateView.dayOfMonth,
                    endView.hour, endView.minute)
                putExtra(editedWorkPeriodKey, WorkPeriodCorrection(
                    start.toDate(),
                    end.toDate()
                ))
            })
            finish()
        }

        refreshViews(editedWorkPeriod)
    }

    private fun refreshViews(editedWorkPeriod: WorkPeriodCorrection) {
        val start = LocalDateTime(editedWorkPeriod.start)
        val end = LocalDateTime(editedWorkPeriod.end)
        dateView.updateDate(start.year, start.monthOfYear, start.dayOfMonth)
        startTime.hour = start.hourOfDay
        startTime.minute = start.minuteOfHour
        endView.hour = end.hourOfDay
        endView.minute = end.minuteOfHour
    }
}