package de.codecentric.trakka_app.ui

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.codecentric.trakka_app.R
import de.codecentric.trakka_app.model.CompanyMembership
import de.codecentric.trakka_app.model.CompanyMembership.Companion.NoCompany


class CompanySelectViewHolder(val view: View) {
    val image: ImageView = view.findViewById(R.id.company_logo)
    val text: TextView = view.findViewById(R.id.company_name)

    var company : CompanyMembership = NoCompany
        set(value) {
            field = value
            value.logoURL?.let {
                runCatching {
                    image.setImageURI(Uri.parse(it))
                }
            }
            text.text = value.friendlyName
        }
}