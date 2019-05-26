package se.gustavkarlsson.skylight.android.feature.about

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.feature.base.Destination
import se.gustavkarlsson.skylight.android.feature.base.DestinationRegistry
import se.gustavkarlsson.skylight.android.services.RunVersionManager

val aboutModule = module {

	viewModel {
		// FIXME make sure this flavor thing works
		val isDevelopMode = BuildConfig.FLAVOR.contains("develop", true)
		AboutViewModel(
			time = get(),
			isDevelopMode = isDevelopMode,
			versionCode = get("versionCode"),
			versionName = get("versionName"),
			gitBranch = BuildConfig.GIT_BRANCH,
			gitSha1 = BuildConfig.GIT_SHA1,
			buildTime = Instant.ofEpochMilli(BuildConfig.BUILD_TIME_MILLIS)
		)
	}

	single<ModuleStarter>("about") {
		object : ModuleStarter {
			override fun start() {
				val destination = Destination("about", 0, false) { id ->
					if (id == "about")
						AboutFragment()
					else
						null
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}

}
