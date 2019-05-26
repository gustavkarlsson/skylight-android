package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.BuildConfig

val appModule = module {

	single("versionCode") {
		BuildConfig.VERSION_CODE
	}

	single("versionName") {
		BuildConfig.VERSION_NAME
	}

}
