package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.entities.AuroraReport

interface AuroraReportProvider {
    fun get(): AuroraReport
}
