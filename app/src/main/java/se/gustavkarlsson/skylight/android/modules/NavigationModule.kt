package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.feature.base.Navigator

val navigationModule = module {

	scope("activity") {
		Navigator(
			fragmentManager = get(),
			containerId = R.id.fragmentContainer, // FIXME injected?
			destinationRegistry = get()
		)
	}

}
