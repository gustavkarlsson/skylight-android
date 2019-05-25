package se.gustavkarlsson.skylight.android.feature.about

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import se.gustavkarlsson.skylight.android.services.Time

internal class AboutViewModel(
	val isDevelopMode: Boolean,
	time: Time
) : ViewModel() {

	val author: TextRef = TextRef(R.string.about_app_by, TextRef(R.string.author))

	val versionName: TextRef = TextRef(R.string.about_version_name, BuildConfig.VERSION_NAME)

	val versionCode: TextRef = TextRef(R.string.about_version_code, BuildConfig.VERSION_CODE)

	val buildTime: TextRef = let {
		val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
		val zoneId = time.zoneId().blockingGet()
		val instant = Instant.ofEpochMilli(BuildConfig.BUILD_TIME_MILLIS)
		val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
		val formattedTime = formatter.format(localDateTime)
		TextRef(R.string.about_built_on, formattedTime)
	}

	val branch: TextRef = TextRef(R.string.about_branch, BuildConfig.GIT_BRANCH)

	val sha1Compact: TextRef = TextRef(R.string.about_sha1, BuildConfig.GIT_SHA1.substring(0, 7))

	val privacyPolicyLink: TextRef =
		TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))
}
