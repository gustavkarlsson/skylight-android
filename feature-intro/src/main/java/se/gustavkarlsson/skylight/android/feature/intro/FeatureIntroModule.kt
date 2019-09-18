package se.gustavkarlsson.skylight.android.feature.intro

import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.NavItemOverride
import se.gustavkarlsson.skylight.android.lib.navigation.NavItemOverrideRegistry

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
				val override = object : NavItemOverride {
					override val priority: Int = 5
					override fun override(item: NavItem): NavItem? =
						if (runVersionManager.isFirstRun) {
							NavItem("intro", "intro") { "destination" to item }
						} else null
				}
				get<NavItemOverrideRegistry>().register(override)

				val fragmentFactory = object : FragmentFactory {
					override fun createFragment(name: String): Fragment? =
						if (name == "intro") IntroFragment()
						else null
				}
				get<FragmentFactoryRegistry>().register(fragmentFactory)
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
