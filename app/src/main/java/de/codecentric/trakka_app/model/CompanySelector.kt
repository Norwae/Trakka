package de.codecentric.trakka_app.model

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import java.util.concurrent.CopyOnWriteArraySet

private const val currentCompanyField = "currentCompany"

object CompanySelector {
    val companySelectionChangeListeners =
        CopyOnWriteArraySet<UpdateListener<List<CompanyMembership>>>()

    val companyChangeListeners =
        CopyOnWriteArraySet<UpdateListener<CompanyMembership>>()

    var currentCompany by UpdateDispatcher(CompanyMembership.NoCompany, companyChangeListeners)
        private set

    var allCompanies by UpdateDispatcher(emptyList(), companySelectionChangeListeners)
        private set

    private var selectedId = ""
    private lateinit var userDocument: DocumentReference

    fun selectNewUser(userId: String) {
        userDocument.update(currentCompanyField, userId)
    }

    private fun refreshCurrentCompany() {
        currentCompany = allCompanies.find {
            it.id == selectedId
        } ?: allCompanies.firstOrNull() ?: CompanyMembership.NoCompany
        Log.i(TAG, "Set current company to $currentCompany")
    }

    internal fun init(userId: String) {
        userDocument = firestore.document("/users/$userId")
        userDocument.addSnapshotListener { snapshot, err ->
            if (err == null && snapshot != null) {
                selectedId = snapshot.get(currentCompanyField).toString()
                refreshCurrentCompany()
            } else {
                Log.e(TAG, "Company select listener reported refresh error", err)
            }
        }

        firestore.collection("/users/$userId/memberships").addSnapshotListener { snapshots, err ->
            if (err == null && snapshots != null) {
                allCompanies = snapshots.toObjects(CompanyMembership::class.java)
                refreshCurrentCompany()
            } else {
                Log.e(TAG, "Company membership listener reported refresh error", err)
            }
        }

    }
}