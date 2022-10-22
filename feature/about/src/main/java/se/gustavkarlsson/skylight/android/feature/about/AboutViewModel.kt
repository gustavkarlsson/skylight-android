package se.gustavkarlsson.skylight.android.feature.about

import com.ioki.textref.TextRef
import dagger.Reusable
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import se.gustavkarlsson.skylight.android.core.VersionCode
import se.gustavkarlsson.skylight.android.core.VersionName
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Reusable
internal class AboutViewModel(
    private val time: Time,
    private val showDevelopData: Boolean,
    private val versionCode: Int,
    private val versionName: String,
    private val gitBranch: String,
    private val gitSha1: String,
    private val buildTime: Instant,
) : ScopedService {

    @Inject
    constructor(
        time: Time,
        @VersionCode versionCode: Int,
        @VersionName versionName: String,
    ) : this(
        time = time,
        showDevelopData = BuildConfig.DEVELOP,
        versionCode = versionCode,
        versionName = versionName,
        gitBranch = BuildConfig.GIT_BRANCH,
        gitSha1 = BuildConfig.GIT_SHA1,
        buildTime = Instant.fromEpochMilliseconds(BuildConfig.BUILD_TIME_MILLIS),
    )

    val detailsText: TextRef = createDetailsText()

    private fun createDetailsText(): TextRef {
        val lines = getTextLines()
        val formatString = lines.joinToString(separator = "\n") { "%s" }
        return TextRef.string(formatString, *lines.toTypedArray())
    }

    private fun getTextLines(): List<TextRef> =
        listOfNotNull(
            TextRef.stringRes(R.string.about_app_by, TextRef.stringRes(R.string.author)),
            TextRef.stringRes(R.string.about_version_name, versionName),
            ifDevelop { TextRef.stringRes(R.string.about_version_code, versionCode) },
            ifDevelop { getBuildTime() },
            ifDevelop { TextRef.stringRes(R.string.about_branch, gitBranch) },
            ifDevelop { TextRef.stringRes(R.string.about_sha1, gitSha1.substring(0, 7)) },
        )

    private fun ifDevelop(text: () -> TextRef): TextRef? = if (showDevelopData) text() else null

    private fun getBuildTime(): TextRef {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val timeZone = time.timeZone()
        val localDateTime = buildTime.toLocalDateTime(timeZone)
        val formattedTime = formatter.format(localDateTime.toJavaLocalDateTime())
        return TextRef.stringRes(R.string.about_built_on, formattedTime)
    }
}
