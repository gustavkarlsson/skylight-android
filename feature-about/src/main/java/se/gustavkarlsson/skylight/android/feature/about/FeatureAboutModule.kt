package se.gustavkarlsson.skylight.android.feature.about

import org.koin.dsl.module.module
import org.threeten.bp.Instant

val featureAboutModule = module {

    factory {
        AboutViewModel(
            time = get(),
            showDevelopData = BuildConfig.DEVELOP,
            versionCode = get("versionCode"),
            versionName = get("versionName"),
            gitBranch = BuildConfig.GIT_BRANCH,
            gitSha1 = BuildConfig.GIT_SHA1,
            buildTime = Instant.ofEpochMilli(BuildConfig.BUILD_TIME_MILLIS)
        )
    }
}
