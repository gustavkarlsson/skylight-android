package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

interface GooglePlayServicesModule {
	val googlePlayServicesChecker: GooglePlayServicesChecker
}
