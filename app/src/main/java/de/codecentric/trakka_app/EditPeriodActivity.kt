package de.codecentric.trakka_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import de.codecentric.trakka_app.ui.WorkPeriodCorrection
import kotlinx.android.synthetic.main.edit_period.*
import org.joda.time.LocalDateTime

const val editedWorkPeriodKey = "EditedWorkPeriod"

class EditPeriodActivity : AppCompatActivity() {
    private lateinit var dateView: DatePicker
    private lateinit var startView: TimePicker
    private lateinit var endView: TimePicker

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_period_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

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
        refreshViews(editedWorkPeriod)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_save -> {
                completeEdit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun completeEdit() {

        setResult(Activity.RESULT_OK, Intent().apply {
            val start = LocalDateTime(
                dateView.year, dateView.month, dateView.dayOfMonth,
                startView.currentHour, startView.currentMinute
            )
            val end = LocalDateTime(
                dateView.year, dateView.month, dateView.dayOfMonth,
                endView.currentHour, endView.currentMinute
            )
            putExtra(
                editedWorkPeriodKey, WorkPeriodCorrection(
                    start.toDate(),
                    end.toDate()
                )
            )
        })
        finish()
    }

    private fun refreshViews(editedWorkPeriod: WorkPeriodCorrection) {
        val start = LocalDateTime(editedWorkPeriod.start)
        val end = LocalDateTime(editedWorkPeriod.end)
        dateView.updateDate(start.year, start.monthOfYear, start.dayOfMonth)
        startTime.currentHour = start.hourOfDay
        startTime.currentMinute = start.minuteOfHour
        endView.currentHour = end.hourOfDay
        endView.currentMinute = end.minuteOfHour
    }
}