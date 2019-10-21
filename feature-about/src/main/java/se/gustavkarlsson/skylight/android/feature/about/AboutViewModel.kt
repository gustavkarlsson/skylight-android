package se.gustavkarlsson.skylight.android.feature.about

import androidx.lifecycle.ViewModel
import com.ioki.textref.TextRef
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import se.gustavkarlsson.skylight.android.services.Time

internal class AboutViewModel(
	private val time: Time,
	private val showDevelopData: Boolean,
	private val versionCode: Int,
	private val versionName: String,
	private val gitBranch: String,
	private val gitSha1: String,
	private val buildTime: Instant?
) : ViewModel() {

	val detailsText: TextRef = createDetailsText()

	private fun createDetailsText(): TextRef {
		val lines = getTextLines()
		val formatString = lines.joinToString(separator = "\n") { "%s" }
		return TextRef(formatString, *lines.toTypedArray())
	}

	private fun getTextLines(): List<TextRef> =
		listOfNotNull(
			TextRef(R.string.about_app_by, TextRef(R.string.author)),
			TextRef(R.string.about_version_name, versionName),
			ifDevelop { TextRef(R.string.about_version_code, versionCode) },
			ifDevelop { getBuildTime() },
			ifDevelop { TextRef(R.string.about_branch, gitBranch) },
			ifDevelop { TextRef(R.string.about_sha1, gitSha1.substring(0, 7)) }
		)

	private fun ifDevelop(text: () -> TextRef): TextRef? = if (showDevelopData) text() else null

	private fun getBuildTime(): TextRef {
		val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
		val zoneId = time.zoneId()
		val localDateTime = LocalDateTime.ofInstant(buildTime, zoneId)
		val formattedTime = formatter.format(localDateTime)
		return TextRef(R.string.about_built_on, formattedTime)
	}

	val privacyPolicyLink: TextRef =
		TextRef(R.string.html_privacy_policy_link, TextRef(R.string.privacy_policy))
}
