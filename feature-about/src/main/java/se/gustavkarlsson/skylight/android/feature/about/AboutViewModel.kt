package se.gustavkarlsson.skylight.android.feature.about

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import se.gustavkarlsson.skylight.android.services.Time

internal class AboutViewModel(
	time: Time,
	val isDevelopMode: Boolean,
	versionCode: Int,
	versionName: String,
	gitBranch: String,
	gitSha1: String,
	buildTime: Instant?
) : ViewModel() {

	val author: TextRef = TextRef(R.string.about_app_by, TextRef(R.string.author))

	val versionName: TextRef = TextRef(R.string.about_version_name, versionName)

	val versionCode: TextRef = TextRef(R.string.about_version_code, versionCode)

	val buildTime: TextRef = let {
		val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
		val zoneId = time.zoneId().blockingGet()
		val localDateTime = LocalDateTime.ofInstant(buildTime, zoneId)
		val formattedTime = formatter.format(localDateTime)
		TextRef(R.string.about_built_on, formattedTime)
	}

	val branch: TextRef = TextRef(R.string.about_branch, gitBranch)

	val sha1Compact: TextRef = TextRef(R.string.about_sha1, gitSha1.substring(0, 7))

	val privacyPolicyLink: TextRef =
		TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))
}
