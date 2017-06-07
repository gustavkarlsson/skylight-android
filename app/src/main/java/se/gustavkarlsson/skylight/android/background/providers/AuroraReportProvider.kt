package se.gustavkarlsson.skylight.android.background.providers

import org.threeten.bp.Duration

import se.gustavkarlsson.skylight.android.models.AuroraReport

internal interface AuroraReportProvider {
    fun getReport(timeout: Duration): AuroraReport
}
