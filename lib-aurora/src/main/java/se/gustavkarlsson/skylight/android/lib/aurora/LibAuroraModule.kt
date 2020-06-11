package se.gustavkarlsson.skylight.android.lib.aurora

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.lib.weather.WeatherProvider
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.DarknessProvider
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.ReverseGeocoder

@Module
class LibAuroraModule {

    @Provides
    @Reusable
    internal fun chanceLevelFormatter(): Formatter<ChanceLevel> = ChanceLevelFormatter

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

    @Provides
    @Reusable
    internal fun completeAuroraReportChanceEvaluator(
        kpIndexEvaluator: ChanceEvaluator<KpIndex>,
        geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
        weatherEvaluator: ChanceEvaluator<Weather>,
        darknessEvaluator: ChanceEvaluator<Darkness>
    ): ChanceEvaluator<CompleteAuroraReport> =
        CompleteAuroraReportEvaluator(
            kpIndexEvaluator,
            geomagLocationEvaluator,
            weatherEvaluator,
            darknessEvaluator
        )
}
