package se.gustavkarlsson.skylight.android.lib.aurora

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent

@Component
abstract class AuroraComponent internal constructor(
    @Component internal val darknessComponent: DarknessComponent,
    @Component internal val geomagLocationComponent: GeomagLocationComponent,
    @Component internal val kpIndexComponent: KpIndexComponent,
    @Component internal val weatherComponent: WeatherComponent,
) {

    abstract val auroraReportProvider: AuroraReportProvider

    abstract val auroraForecastReportProvider: AuroraForecastReportProvider

    @get:Provides
    val chanceLevelFormatter: Formatter<ChanceLevel> = ChanceLevelFormatter

    abstract val auroraReportChanceEvaluator: ChanceEvaluator<AuroraReport>

    @Provides
    internal fun auroraReportProvider(
        impl: CombiningAuroraReportProvider,
    ): AuroraReportProvider = impl

    @Provides
    internal fun completeAuroraReportChanceEvaluator(
        impl: AuroraReportEvaluator,
    ): ChanceEvaluator<AuroraReport> = impl

    companion object {
        val instance: AuroraComponent = AuroraComponent::class.create(
            darknessComponent = DarknessComponent.instance,
            geomagLocationComponent = GeomagLocationComponent.instance,
            kpIndexComponent = KpIndexComponent.instance,
            weatherComponent = WeatherComponent.instance,
        )
    }
}
