package se.gustavkarlsson.skylight.android.di.modules

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

interface LocationNameModule {
	val locationNameProvider: LocationNameProvider
	val locationNameStreamable: Streamable<Optional<String>>
	val locationNameFlowable: Flowable<Optional<String>>
}
