package se.gustavkarlsson.skylight.android.feature.intro

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.ui.Destination
import se.gustavkarlsson.skylight.android.lib.ui.DestinationRegistry
import se.gustavkarlsson.skylight.android.services.RunVersionManager

val introModule = module {

	viewModel { (targetId: String) ->
		IntroViewModel(
			navigator = get(),
			versionManager = get(),
			targetId = targetId
		)
	}

	single<ModuleStarter>("intro") {
		val runVersionManager = get<RunVersionManager>()
		object : ModuleStarter {
			override fun start() {
				val destination = Destination("intro", 5, false) { id ->
					if (runVersionManager.isFirstRun) {
						IntroFragment.newInstance(id)
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
