package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.entities.AuroraReport

interface NewAuroraReportProvider {
	fun get(): AuroraReport
}
