package de.codecentric.trakka_app.model

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
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        return InitState.NeedsAuthentication
    } else {
        CompanySelector.init(user.uid)
        Bookings.init(user.uid)

        Workperiods.listeners += object : UpdateListener<List<Workperiod>> {
            override fun onUpdated(oldValue: List<Workperiod>, newValue: List<Workperiod>) {
                Workperiods.listeners -= this
                onCompleteListener()
            }
        }
        Workperiods.init()
    }

    return InitState.Initializing
}