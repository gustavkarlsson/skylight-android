package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.entities.AuroraReport

// TODO Combine with NewAuroraReportProvider and use annotations to separate
interface LastAuroraReportProvider {
	fun get(): AuroraReport
}
