package se.gustavkarlsson.skylight.android.feature.intro

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.ui.Destination
import se.gustavkarlsson.skylight.android.lib.ui.DestinationRegistry

val featureIntroModule = module {

	viewModel {
		IntroViewModel(
			navigator = get(),
			versionManager = get()
		)
	}

	single<ModuleStarter>("intro") {
		val runVersionManager = get<RunVersionManager>()
		object : ModuleStarter {
			override fun start() {
				val destination = Destination(5) {
					if (runVersionManager.isFirstRun) {
						IntroFragment()
					} else {
						null
					}
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}

	single<RunVersionManager> {
		SharedPreferencesRunVersionManager(
			context = get(),
			currentVersionCode = get("versionCode")
		)
	}

}
