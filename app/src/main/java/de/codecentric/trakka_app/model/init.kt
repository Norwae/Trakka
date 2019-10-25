package de.codecentric.trakka_app.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class InitState {
    NeedsAuthentication,
    Initializing
}

const val TAG = "MODEL"

internal val firestore: FirebaseFirestore
    get() = FirebaseFirestore.getInstance()

fun initialize(onCompleteListener: () -> Unit): InitState {
    Log.i(TAG, "Initializing model")
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        return InitState.NeedsAuthentication
    } else {
        CompanySelector.init(user.uid)
        Bookings.init(user.uid)

        WorkPeriods.listeners += object : UpdateListener<List<Workperiod>> {
            override fun onUpdated(oldValue: List<Workperiod>, newValue: List<Workperiod>) {
                WorkPeriods.listeners -= this
                onCompleteListener()
            }
        }
        WorkPeriods.init()
    }

    return InitState.Initializing
}