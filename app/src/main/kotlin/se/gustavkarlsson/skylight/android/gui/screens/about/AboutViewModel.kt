package se.gustavkarlsson.skylight.android.gui.screens.about

import androidx.lifecycle.ViewModel
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.services.providers.Time

class AboutViewModel(
	val isDevelopMode: Boolean,
	time: Time
) : ViewModel() {

	val branch: String = BuildConfig.GIT_BRANCH

	val sha1Compact: String = BuildConfig.GIT_SHA1.substring(0, 7)

	val versionName: String = BuildConfig.VERSION_NAME

	val versionCode: String = BuildConfig.VERSION_CODE.toString()

	val buildTime: String = let {
		val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
		val zoneId = time.zoneId().blockingGet()
		val instant = Instant.ofEpochMilli(BuildConfig.BUILD_TIME_MILLIS)
		val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
		formatter.format(localDateTime)
	}
}
