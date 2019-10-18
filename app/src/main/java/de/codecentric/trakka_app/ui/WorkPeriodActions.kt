package de.codecentric.trakka_app.ui

import de.codecentric.trakka_app.workperiod.Workperiod
import java.util.*

interface WorkPeriodActions {
    fun revoke(period: Workperiod)
    fun edit(period: Workperiod)
}