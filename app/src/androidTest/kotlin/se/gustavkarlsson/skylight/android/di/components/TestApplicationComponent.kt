package se.gustavkarlsson.skylight.android.di.components

import android.app.Application
import com.hadisatrio.optional.Optional
import se.gustavkarlsson.skylight.android.di.modules.*
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.test.TestLocationNameProvider
import se.gustavkarlsson.skylight.android.test.TestLocationProvider

class TestSkylightAppComponent(openWeatherMapApiKey: String, application: Application) :
	SkylightAppComponent(openWeatherMapApiKey, application) {

	override val analyticsModule: AnalyticsModule by lazy {
		NullAnalyticsModule()
	}

	override val sharedPreferencesModule: SharedPreferencesModule by lazy {
		TestSharedPreferencesModule(contextModule.context)
	}

	override val locationModule: LocationModule by lazy {
		TestLocationModule(testLocationProvider)
	}

	override val locationNameModule: LocationNameModule by lazy {
		TestLocationNameModule(locationModule.locationFlowable, testLocationNameProvider)
	}

	val testLocationProvider: TestLocationProvider =
		TestLocationProvider({ Optional.of(Location(0.0, 0.0)) })

	val testLocationNameProvider: TestLocationNameProvider =
		TestLocationNameProvider({ Optional.of("Garden of Eden") })
}
