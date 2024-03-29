package de.codecentric.trakka_app.model

data class CompanyMembership(
    var id: String = "",
    var friendlyName: String = "",
    var logoURL: String? = null
) {
    companion object {
        val NoCompany = CompanyMembership("-1", "<No Company>")
    }
}
