package de.codecentric.trakka_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import de.codecentric.trakka_app.model.*
import de.codecentric.trakka_app.ui.WorkPeriodActions
import de.codecentric.trakka_app.ui.WorkPeriodAdapter
import de.codecentric.trakka_app.ui.WorkPeriodCorrection
import org.joda.time.DateTime
import org.joda.time.Duration

private const val RC_SIGN_IN = 0x8378
private const val RC_EDIT_WORK_PERIOD = 0x2171
private const val RC_EDIT_NEW_PERIOD = 0x2170
private const val RC_SETTINGS = 0x2172

private const val INIT_TAG = "INIT"

class MainActivity : AppCompatActivity() {

    private val firebaseAuthProviders = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    private var editedReference: String? = null

    private fun onToggleClicked(view: View) {
        val head = WorkPeriods.workperiods.firstOrNull()
        val now = DateTime()

        if (head != null && head.end == null) {
            if (Duration(head.start, now).standardMinutes < 1) {
                Bookings.revoke(head.rootId)
            } else {
                Bookings.end(head.rootId)
            }
        } else {
            Bookings.start()
        }
    }

    private fun initModel() {
        val result = initialize {
            Log.i(INIT_TAG, "Completed initial refresh")
        }

        if (result == InitState.NeedsAuthentication) {
            Log.i(INIT_TAG, "Performing login")
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(firebaseAuthProviders)
                    .build(),
                RC_SIGN_IN
            )
        } else {
            Log.d(INIT_TAG, "Already authenticated")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_EDIT_NEW_PERIOD -> {
                val result = data?.extras?.get(editedWorkPeriodKey) as? WorkPeriodCorrection
                result?.let {
                    Bookings.block(it.start, it.end)
                }
            }
            RC_EDIT_WORK_PERIOD -> {
                val result = data?.extras?.get(editedWorkPeriodKey) as? WorkPeriodCorrection
                result?.let {
                    Bookings.correct(editedReference!!, it.start, it.end)
                }
            }
            RC_SIGN_IN -> initModel()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_new -> {
                editedReference = null
                val intent = Intent(this, EditPeriodActivity::class.java).apply {
                    val now = DateTime.now()
                    putExtra(editedWorkPeriodKey, WorkPeriodCorrection(now, now))
                }
                startActivityForResult(intent, RC_EDIT_NEW_PERIOD)
                false
            }
            R.id.action_settings -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), RC_SETTINGS)
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = WorkPeriodAdapter(object : WorkPeriodActions {
            override fun correct(reference: String, start: DateTime, end: DateTime) {
                editedReference = reference
                val intent = Intent(this@MainActivity, EditPeriodActivity::class.java).apply {
                    putExtra(editedWorkPeriodKey, WorkPeriodCorrection(start, end))
                }

                startActivityForResult(intent, RC_EDIT_WORK_PERIOD)
            }
        })

        val listView = findViewById<RecyclerView>(R.id.bookingList)
        listView.setHasFixedSize(true)
        listView.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.VERTICAL
        }
        listView.adapter = adapter

        val addButton = findViewById<Button>(R.id.toggleButton)
        addButton.setOnClickListener(this::onToggleClicked)

        WorkPeriods.listeners += (object : UpdateListener<List<Workperiod>> {
            override fun onUpdated(oldValue: List<Workperiod>, newValue: List<Workperiod>) {
                adapter.updateContents(newValue)
                addButton.text = resources.getText(
                    if (newValue.isEmpty() || newValue.first().end != null) R.string.toggle_on
                    else R.string.toggle_off
                )
            }
        })

        initModel()
    }

}
