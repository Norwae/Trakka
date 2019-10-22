package de.codecentric.trakka_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import de.codecentric.trakka_app.ui.TextualCalendarFragment
import de.codecentric.trakka_app.ui.WorkPeriodCorrection
import kotlinx.android.synthetic.main.edit_period.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

const val editedWorkPeriodKey = "EditedWorkPeriod"

class EditPeriodActivity : AppCompatActivity() {
    private lateinit var date: TextualCalendarFragment
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

        date = supportFragmentManager.findFragmentById(R.id.dateFragment) as TextualCalendarFragment
        startView = findViewById(R.id.startTime)
        endView = findViewById(R.id.endTime)

        startView.setIs24HourView(true)
        endView.setIs24HourView(true)
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
            val value = date.currentValue
            if (value != null) {
                val start = DateTime(
                    value.year, value.monthOfYear, value.dayOfMonth,
                    startView.currentHour, startView.currentMinute
                )
                val end = DateTime(
                    value.year, value.monthOfYear, value.dayOfMonth,
                    endView.currentHour, endView.currentMinute
                )
                putExtra(
                    editedWorkPeriodKey, WorkPeriodCorrection(
                        start,
                        end
                    )
                )
            }
        })
        finish()
    }

    private fun refreshViews(editedWorkPeriod: WorkPeriodCorrection) {
        val start = LocalDateTime(editedWorkPeriod.start)
        val end = LocalDateTime(editedWorkPeriod.end)
        date.currentValue = start.toLocalDate()
        startTime.currentHour = start.hourOfDay
        startTime.currentMinute = start.minuteOfHour
        endView.currentHour = end.hourOfDay
        endView.currentMinute = end.minuteOfHour
    }
}