package se.gustavkarlsson.skylight.android.background.providers

import org.threeten.bp.Duration

import se.gustavkarlsson.skylight.android.entities.AuroraReport

interface AuroraReportProvider {
    fun getReport(timeout: Duration): AuroraReport
}
