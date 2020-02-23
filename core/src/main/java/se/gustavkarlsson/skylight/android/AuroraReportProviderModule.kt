package se.gustavkarlsson.skylight.android

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.DarknessProvider
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.ReverseGeocoder
import se.gustavkarlsson.skylight.android.services.WeatherProvider

@Module
class AuroraReportProviderModule {

    @Provides
    @Reusable
    internal fun auroraReportProvider(
        reverseGeocoder: ReverseGeocoder,
        darknessProvider: DarknessProvider,
        geomagLocationProvider: GeomagLocationProvider,
        kpIndexProvider: KpIndexProvider,
        weatherProvider: WeatherProvider
    ): AuroraReportProvider =
        CombiningAuroraReportProvider(
            reverseGeocoder,
            darknessProvider,
            geomagLocationProvider,
            kpIndexProvider,
            weatherProvider
        )
}
