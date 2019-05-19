package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.modules.testGooglePlayServicesModule
import se.gustavkarlsson.skylight.android.modules.testLocationModule
import se.gustavkarlsson.skylight.android.modules.testLocationNameModule
import se.gustavkarlsson.skylight.android.modules.testPermissionsModule
import se.gustavkarlsson.skylight.android.modules.testRunVersionsModule
import se.gustavkarlsson.skylight.android.modules.testSharedPreferencesModule

val extraModules = listOf(
	testLocationModule,
	testLocationNameModule,
	testSharedPreferencesModule,
	testRunVersionsModule,
	testPermissionsModule,
	testGooglePlayServicesModule
)
