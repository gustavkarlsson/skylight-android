package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME
import se.gustavkarlsson.skylight.android.models.AuroraFactors
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.models.factors.Darkness
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation
import se.gustavkarlsson.skylight.android.models.factors.Visibility
import se.gustavkarlsson.skylight.android.observers.ObservableValue
import javax.inject.Named
import javax.inject.Singleton

@Module
class LatestAuroraReportObservableModule {

    @Provides
    @Singleton
    @Named(LATEST_NAME)
    fun provideLatestAuroraReportObservable(@Named(LATEST_NAME) cache: SingletonCache<AuroraReport>, clock: Clock): ObservableValue<AuroraReport> {
        var report = cache.value
        if (report == null) {
            val factors = AuroraFactors(
                    GeomagActivity(null),
                    GeomagLocation(null),
                    Darkness(null),
                    Visibility(null)
            )
            report = AuroraReport(clock.millis(), null, factors)
        }
        return ObservableValue<AuroraReport>(report)
    }
}
