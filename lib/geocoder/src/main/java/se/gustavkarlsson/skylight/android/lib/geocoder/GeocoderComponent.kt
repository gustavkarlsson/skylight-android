package se.gustavkarlsson.skylight.android.lib.geocoder

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.core.CoreComponent

@Component
abstract class GeocoderComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
) {

    abstract val geocoder: Geocoder

    @Provides
    internal fun geocoder(impl: MapboxGeocoder): Geocoder = impl

    companion object {
        val instance: GeocoderComponent = GeocoderComponent::class.create(
            coreComponent = CoreComponent.instance,
        )
    }
}
