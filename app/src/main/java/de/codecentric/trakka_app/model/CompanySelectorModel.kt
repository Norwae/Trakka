package de.codecentric.trakka_app.model

import com.google.firebase.firestore.FirebaseFirestore

class CompanySelectorModel(
    private val firebase: FirebaseFirestore,
    userId: String) {

    private var workerSetup: WorkerSetup? = null
    private var memberships: List<CompanyMembership>? = null

    val currentCompany: CompanyMembership
        get() {
            return memberships?.find {
                it.id == workerSetup?.preferredCompany
            } ?: memberships?.firstOrNull() ?: CompanyMembership("-1", "<No Company>")
        }


    init {
        firebase.document("/users/${userId}").addSnapshotListener { user, err ->
            if (err == null && user != null) {
                workerSetup = user.toObject(WorkerSetup::class.java)!!
            }
        }

        firebase.collection("/users/${userId}/memberships").addSnapshotListener { member, err ->
            if (err == null && member != null) {
                memberships = member.toObjects(CompanyMembership::class.java)
            }
        }
    }
}