package se.gustavkarlsson.skylight.android

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.evaluators.CompleteAuroraReportEvaluator
import se.gustavkarlsson.skylight.android.evaluators.DarknessEvaluator
import se.gustavkarlsson.skylight.android.evaluators.GeomagLocationEvaluator
import se.gustavkarlsson.skylight.android.evaluators.KpIndexEvaluator
import se.gustavkarlsson.skylight.android.evaluators.WeatherEvaluator
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

@Module
class EvaluatorsModule {

    @Provides
    @Reusable
    internal fun kpIndexEvaluator(): ChanceEvaluator<KpIndex> = KpIndexEvaluator

    @Provides
    @Reusable
    internal fun geomagLocationEvaluator(): ChanceEvaluator<GeomagLocation> =
        GeomagLocationEvaluator

    @Provides
    @Reusable
    internal fun weatherEvaluator(): ChanceEvaluator<Weather> = WeatherEvaluator

    @Provides
    @Reusable
    internal fun darknessEvaluator(): ChanceEvaluator<Darkness> = DarknessEvaluator

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
