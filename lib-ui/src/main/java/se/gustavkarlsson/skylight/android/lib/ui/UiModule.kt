package se.gustavkarlsson.skylight.android.lib.ui

import org.koin.dsl.module.module

val uiModule = module {

	single {
		DestinationRegistry()
	}
}
