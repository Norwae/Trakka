package de.codecentric.trakka_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import de.codecentric.trakka_app.model.CompanyMembership
import de.codecentric.trakka_app.model.CompanySelector
import de.codecentric.trakka_app.ui.CompanySelectAdapter

class SettingsActivity : AppCompatActivity() {
    private lateinit var spinner: Spinner

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_save -> {
                val companyMembership = spinner.selectedItem as CompanyMembership
                CompanySelector.selectNewUser(companyMembership.id)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_editor_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val allCompanies = CompanySelector.allCompanies

        spinner = findViewById(R.id.spinner)

        spinner.adapter = CompanySelectAdapter(this, allCompanies)
        val idx = allCompanies.indexOf(CompanySelector.currentCompany)
        spinner.setSelection(idx)
    }
}