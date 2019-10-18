package de.codecentric.trakka_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.codecentric.trakka_app.model.Booking
import de.codecentric.trakka_app.model.BookingKind
import de.codecentric.trakka_app.ui.WorkPeriodActions
import de.codecentric.trakka_app.ui.WorkPeriodAdapter
import de.codecentric.trakka_app.ui.WorkPeriodCorrection
import de.codecentric.trakka_app.workperiod.Workperiod
import de.codecentric.trakka_app.workperiod.Workperiods
import org.joda.time.Duration
import java.util.*


const val RC_SIGN_IN = 0x8378
const val RC_EDIT_WORK_PERIOD = 0x2171
const val company = "Foo"

class MainActivity : AppCompatActivity(), WorkPeriodActions {

    private val user: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    // Choose authentication providers
    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    private val firestore = FirebaseFirestore.getInstance()

    private val collection by lazy {
        firestore.collection("users/${user?.uid}/bookings")
    }

    private lateinit var addButton: Button
    private var editedReference: String? = null

    override fun revoke(period: Workperiod) {
        collection.add(Booking(BookingKind.RETRACTION, reference = period.rootId, company = company))
    }

    override fun edit(period: Workperiod) {
        editedReference = period.rootId
        val intent = Intent(this, EditPeriodActivity::class.java).apply {
            putExtra(editedWorkPeriodKey, WorkPeriodCorrection(period.start, period.end!!))
        }

        startActivityForResult(intent, RC_EDIT_WORK_PERIOD)
    }

    private fun onToggleClicked(view: View) {
        val head = Workperiods.workperiods.firstOrNull()
        val now = Date()

        val booking = if (head != null && head.end == null) {
            if (Duration(head.start.time, now.time).standardMinutes < 1) {
                Booking(BookingKind.RETRACTION, company = company, reference = head.rootId)
            } else {
                Booking(BookingKind.END, end = now, company = company, reference = head.rootId)
            }
        } else {
            Booking(BookingKind.START, start = now, company = company)
        }
        collection.add(booking)
    }

    private fun onLoggedIn() {
        collection.addSnapshotListener(Workperiods.updateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("ACTIVITY", "Result: request $requestCode, result: $resultCode, data: $data")

        when (requestCode) {
            RC_EDIT_WORK_PERIOD -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result = data?.extras?.get(editedWorkPeriodKey) as? WorkPeriodCorrection
                    result?.run(this::returnedFromEdit)
                }
                editedReference = null
            }
            RC_SIGN_IN -> if (resultCode == Activity.RESULT_OK) {
                onLoggedIn()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun returnedFromEdit(correction: WorkPeriodCorrection) {
        collection.add(Booking(BookingKind.CORRECTION, correction.start, correction.end, company = company, reference = editedReference))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter = WorkPeriodAdapter(this)
        setContentView(R.layout.activity_main)

        val listView = findViewById<RecyclerView>(R.id.bookingList)
        listView.setHasFixedSize(true)
        listView.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.VERTICAL
        }
        listView.adapter = adapter
        Workperiods.listeners += {
            adapter.updateContents(it)
            addButton.text = determineToggleLabel(it)
        }

        addButton = findViewById<Button>(R.id.toggleButton)
        addButton.setOnClickListener(this::onToggleClicked)


        if (user == null) {
            Log.i("INIT", "Performing login")
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        } else {
            Log.d("INIT", "Already authenticated as ${user?.uid}")
            onLoggedIn()
        }

    }

    private fun determineToggleLabel(it: List<Workperiod>) =
        resources.getText(
            if (it.firstOrNull()?.end != null) R.string.toggle_on
            else R.string.toggle_off
        )

}
