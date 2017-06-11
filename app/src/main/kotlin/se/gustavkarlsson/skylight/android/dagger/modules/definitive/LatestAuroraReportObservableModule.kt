package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.models.*
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
            report = AuroraReport(clock.now, null, factors)
        }
        return ObservableValue<AuroraReport>(report)
    }
}
