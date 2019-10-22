package de.codecentric.trakka_app

import android.app.Activity
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.codecentric.trakka_app.model.Booking
import de.codecentric.trakka_app.model.BookingKind
import de.codecentric.trakka_app.model.CompanyMembership
import de.codecentric.trakka_app.model.CompanySelectorModel
import de.codecentric.trakka_app.ui.WorkPeriodActions
import de.codecentric.trakka_app.ui.WorkPeriodAdapter
import de.codecentric.trakka_app.ui.WorkPeriodCorrection
import de.codecentric.trakka_app.workperiod.Workperiod
import de.codecentric.trakka_app.workperiod.Workperiods
import org.joda.time.Duration
import java.util.*


const val RC_SIGN_IN = 0x8378
const val RC_EDIT_WORK_PERIOD = 0x2171
const val RC_EDIT_NEW_PERIOD = 0x2170

class MainActivity : AppCompatActivity(), WorkPeriodActions {

    private val user: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    // Choose authentication providers
    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    private val firestore = FirebaseFirestore.getInstance()

    private var companyModel: CompanySelectorModel? = null
    private val company: String?
        get() = companyModel?.currentCompany?.id

    private val bookings by lazy {
        firestore.collection("users/${user?.uid}/bookings")
    }


    
    private lateinit var addButton: Button
    private var editedReference: String? = null

    override fun revoke(period: Workperiod) {
        company?.let {
            bookings.add(Booking(BookingKind.RETRACTION, reference = period.rootId, company = it))
        }
    }

    private fun new() {
        editedReference = null
        val intent = Intent(this, EditPeriodActivity::class.java).apply {
            val now = Date()
            putExtra(editedWorkPeriodKey, WorkPeriodCorrection(now, now))
        }

        startActivityForResult(intent, RC_EDIT_NEW_PERIOD)
    }

    override fun edit(period: Workperiod) {
        editedReference = period.rootId
        val intent = Intent(this, EditPeriodActivity::class.java).apply {
            putExtra(editedWorkPeriodKey, WorkPeriodCorrection(period.start, period.end!!))
        }

        startActivityForResult(intent, RC_EDIT_WORK_PERIOD)
    }

    private fun onToggleClicked(view: View) {
        company?.let { company ->
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
            bookings.add(booking)

        }
    }


    private fun onLoggedIn() {
        companyModel = CompanySelectorModel(firestore, user!!.uid)

        bookings.addSnapshotListener(Workperiods.updateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("ACTIVITY", "Result: request $requestCode, result: $resultCode, data: $data")
        fun handleEditResult(bookingKind: BookingKind) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.extras?.get(editedWorkPeriodKey) as? WorkPeriodCorrection
                result?.let {
                    returnedFromEdit(it, bookingKind)
                }
            }
            editedReference = null
        }

        when (requestCode) {
            RC_EDIT_NEW_PERIOD -> handleEditResult(BookingKind.BLOCK)
            RC_EDIT_WORK_PERIOD -> handleEditResult(BookingKind.CORRECTION)
            RC_SIGN_IN -> if (resultCode == Activity.RESULT_OK) {
                onLoggedIn()
            }
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
                new()
                true
            }
            R.id.action_settings -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun returnedFromEdit(correction: WorkPeriodCorrection, kind: BookingKind) {
        company?.let {
            bookings.add(Booking(kind, correction.start, correction.end, company = it, reference = editedReference))
        }
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

        addButton = findViewById(R.id.toggleButton)
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
