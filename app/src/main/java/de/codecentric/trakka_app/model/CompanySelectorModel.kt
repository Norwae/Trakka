package de.codecentric.trakka_app.model

import android.app.Activity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

interface CompanyChangeListener : EventListener {
    fun onCompanyChanged(company: CompanyMembership)
}

class CompanySelectorModel(
    firebase: FirebaseFirestore,
    owner: Activity,
    userId: String) {

    private var workerSetup: WorkerSetup? = null
    private var memberships: List<CompanyMembership>? = null

    private val listeners = CopyOnWriteArraySet<CompanyChangeListener>()

    fun addCompanyChangeListener(listener: CompanyChangeListener) {
        listeners += listener
    }

    var currentCompany: CompanyMembership
        get() {
            return memberships?.find {
                it.id == workerSetup?.preferredCompany
            } ?: memberships?.firstOrNull() ?: NoCompany
        }
        set(value) {
            workerSetup?.let {
                it.preferredCompany = value.id
            }
        }


    init {
        firebase.document("/users/${userId}").addSnapshotListener(owner) { user, err ->
            if (err == null && user != null) {
                val oldId = workerSetup?.preferredCompany
                workerSetup = user.toObject(WorkerSetup::class.java) ?: WorkerSetup()

                if (workerSetup?.preferredCompany != oldId) {
                    for (l in listeners) {
                        l.onCompanyChanged(currentCompany)
                    }
                }
            }
        }

        firebase.collection("/users/${userId}/memberships").addSnapshotListener(owner) { member, err ->
            if (err == null && member != null) {
                memberships = member.toObjects(CompanyMembership::class.java)
            }
        }
    }
}