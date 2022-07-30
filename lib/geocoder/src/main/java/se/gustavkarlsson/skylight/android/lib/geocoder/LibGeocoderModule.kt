package se.gustavkarlsson.skylight.android.lib.geocoder

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@Module
@ContributesTo(AppScopeMarker::class)
object LibGeocoderModule {

    @Provides
    internal fun geocoder(impl: MapboxGeocoder): Geocoder = impl
}
