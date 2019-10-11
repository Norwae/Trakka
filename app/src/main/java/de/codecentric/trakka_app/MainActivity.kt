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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.codecentric.trakka_app.model.Booking
import de.codecentric.trakka_app.model.BookingKind
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*


const val RC_SIGN_IN = 28910

class MainActivity : AppCompatActivity() {

    private val user: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser
    // Choose authentication providers
    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    private val firestore = FirebaseFirestore.getInstance()
    private val contents = mutableListOf<Booking>()
    private lateinit var listView: ListView

    private val collection by lazy {
        firestore.collection("users/${user?.uid}/bookings")
    }

    private fun onToggleClicked(view: View) {
        val booking = Booking(BookingKind.BLOCK, Date(), Date(), Date(), UUID.randomUUID().toString())
        collection.add(booking)
    }

    private fun onLoggedIn() {
        collection.addSnapshotListener { value, e ->
            if (e == null) {
                val model = value!!.toObjects(Booking::class.java)
                Log.i("UDPATE","Received data $model")
                contents.clear()
                contents.addAll(model)
                resetAdapter()
            }
        }
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
        resetAdapter()

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

    private fun resetAdapter() {
        listView.adapter = ArrayAdapter<Booking>(this, R.layout.list_contents, contents)
    }

}
