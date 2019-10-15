package de.codecentric.trakka_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.codecentric.trakka_app.model.Booking
import de.codecentric.trakka_app.model.BookingKind
import de.codecentric.trakka_app.workperiod.Workperiod
import de.codecentric.trakka_app.workperiod.Workperiods
import java.util.*


const val RC_SIGN_IN = 28910

class MainActivity : AppCompatActivity() {

    private val user: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    // Choose authentication providers
    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var listView: ListView

    private val collection by lazy {
        firestore.collection("users/${user?.uid}/bookings")
    }

    private fun onToggleClicked(view: View) {
        val head = Workperiods.workperiods.firstOrNull()
        val booking = if (head != null && head.end == null) {
            Booking(BookingKind.END, end = Date(), company = "Foo", reference = head.rootId)
        } else {
            Booking(BookingKind.START, start = Date(), company = "Foo")
        }
        collection.add(booking)
    }

    private fun onLoggedIn() {
        collection.addSnapshotListener(Workperiods.updateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                onLoggedIn()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.bookingList)
        Workperiods.listeners += {
            listView.adapter = ArrayAdapter<Workperiod>(this, R.layout.list_contents, it)
        }

        val addButton = findViewById<Button>(R.id.addModelButton)
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

}
