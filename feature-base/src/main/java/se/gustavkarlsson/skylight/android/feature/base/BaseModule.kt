package se.gustavkarlsson.skylight.android.feature.base

import org.koin.dsl.module.module

val baseModule = module {

	single {
		DestinationRegistry()
	}
}
