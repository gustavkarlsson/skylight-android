package se.gustavkarlsson.skylight.android.lib.geomaglocation

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Component
abstract class GeomagLocationComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
) {

    @get:Provides
    val geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation> = GeomagLocationEvaluator

    @get:Provides
    val geomagLocationProvider: GeomagLocationProvider = GeomagLocationProviderImpl

    abstract val geomagLocationFormatter: Formatter<GeomagLocation>

    @Provides
    internal fun geomagLocationFormatter(impl: GeomagLocationFormatter): Formatter<GeomagLocation> = impl

    companion object {
        val instance: GeomagLocationComponent = GeomagLocationComponent::class.create(
            coreComponent = CoreComponent.instance,
        )
    }
}
