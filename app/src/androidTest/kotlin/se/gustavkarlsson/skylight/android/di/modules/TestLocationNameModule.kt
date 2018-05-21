package se.gustavkarlsson.skylight.android.di.modules

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.LocationNameProviderStreamable
import se.gustavkarlsson.skylight.android.test.TestLocationNameProvider

class TestLocationNameModule(
	locationFlowable: Flowable<Location>,
	testLocationNameProvider: TestLocationNameProvider
) : LocationNameModule {

	override val locationNameProvider: LocationNameProvider = testLocationNameProvider

	private val locationNameStreamable: Streamable<Optional<String>> by lazy {
		LocationNameProviderStreamable(locationFlowable, locationNameProvider)
	}

	override val locationNameFlowable: Flowable<Optional<String>> by lazy {
		locationNameStreamable.stream
			.replay(1)
			.refCount()
	}
}
