package se.gustavkarlsson.skylight.android.feature.about

import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry

val featureAboutModule = module {

	viewModel {
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

	single<ModuleStarter>("about") {
		object : ModuleStarter {
			override fun start() {
				val fragmentFactory = object : FragmentFactory {
					override fun createFragment(name: String): Fragment? =
						if (name == "about") AboutFragment()
						else null
				}
				get<FragmentFactoryRegistry>().register(fragmentFactory)
			}
		}
	}

}
