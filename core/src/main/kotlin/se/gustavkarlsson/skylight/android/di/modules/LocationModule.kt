package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

interface LocationModule {
	val locationProvider: LocationProvider
	val locationFlowable: Flowable<Location>
	val locationPermission: String
}
