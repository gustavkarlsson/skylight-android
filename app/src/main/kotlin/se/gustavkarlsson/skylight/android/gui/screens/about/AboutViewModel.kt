package se.gustavkarlsson.skylight.android.gui.screens.about

import androidx.lifecycle.ViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

class AboutViewModel(timeProvider: TimeProvider) : ViewModel() {

	val branch: String = BuildConfig.GIT_BRANCH

	val sha1Compact: String = BuildConfig.GIT_SHA1.substring(0, 7)

	val version: String = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

	val buildTime: String = let {
		val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
		val zoneId = timeProvider.getZoneId().blockingGet()
		val instant = Instant.ofEpochMilli(BuildConfig.BUILD_TIME_MILLIS)
		val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
		formatter.format(localDateTime)
	}
}
