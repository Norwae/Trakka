package de.codecentric.trakka_app.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import de.codecentric.trakka_app.R
import de.codecentric.trakka_app.model.CompanyMembership

class CompanySelectAdapter(ctx: Context, private val values: List<CompanyMembership>) :
    ArrayAdapter<CompanyMembership>(ctx, R.layout.company_display, values) {
    private val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder = if (convertView != null) {
            convertView.tag as CompanySelectViewHolder
        } else {
            val view = inflater.inflate(R.layout.company_display, parent, false)
            val holder = CompanySelectViewHolder(view)
            view.tag = holder
            holder
        }
        holder.company = values[position]

        return holder.view
    }

}