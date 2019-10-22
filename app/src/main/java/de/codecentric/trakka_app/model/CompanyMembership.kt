package de.codecentric.trakka_app.model

val NoCompany = CompanyMembership("-1", "<No Company>")

data class CompanyMembership(
    var id: String = "",
    var friendlyName: String = "",
    var logoURL: String? = null
)
