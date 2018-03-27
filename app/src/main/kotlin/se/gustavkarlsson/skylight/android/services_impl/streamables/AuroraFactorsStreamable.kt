package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import io.reactivex.functions.Function4
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Streamable

class AuroraFactorsStreamable(
	kpIndexes: Flowable<KpIndex>,
	visibilities: Flowable<Visibility>,
	darknesses: Flowable<Darkness>,
	geomagLocations: Flowable<GeomagLocation>
) : Streamable<AuroraFactors> {
	override val stream: Flowable<AuroraFactors> =
		Flowable.combineLatest(kpIndexes, geomagLocations, darknesses, visibilities,
			Function4<KpIndex, GeomagLocation, Darkness, Visibility, AuroraFactors> {
				kpIndex, geomagLocation, darkness, visibility ->
				AuroraFactors(kpIndex, geomagLocation, darkness, visibility)
			})
			.replay(1)
			.refCount()
}
