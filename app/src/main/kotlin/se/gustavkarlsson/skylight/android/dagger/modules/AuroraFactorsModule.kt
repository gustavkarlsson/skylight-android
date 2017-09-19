package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.create
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.GeomagActivityProvider
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.services_impl.cache.DualSingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.auroraReportCacheSerializer
import se.gustavkarlsson.skylight.android.services_impl.providers.GeomagLocationProviderImpl
import se.gustavkarlsson.skylight.android.services_impl.providers.KlausBrunnerDarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RetrofittedGeomagActivityProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.AggregatingAuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapService
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.RetrofittedOpenWeatherMapVisibilityProvider
import javax.inject.Named
import javax.inject.Singleton

// TODO Clean up and factor out dependencies
@Module
class AuroraFactorsModule {

    @Provides
    @Reusable
    fun provideAuroraFactorsProvider(
            geomagActivityProvider: RetrofittedGeomagActivityProvider,
            visibilityProvider: VisibilityProvider,
            darknessProvider: KlausBrunnerDarknessProvider,
            geomagLocProvider: GeomagLocationProviderImpl,
            clock: Clock
    ): AuroraFactorsProvider = AggregatingAuroraFactorsProvider(geomagActivityProvider, visibilityProvider, darknessProvider, geomagLocProvider, clock)

    @Provides
    @Reusable
    fun provideGeomagActivityProvider(
            kpIndexService: KpIndexService
    ): GeomagActivityProvider = RetrofittedGeomagActivityProvider(kpIndexService)

    @Provides
    @Reusable
    fun provideOpenWeatherMapService(): OpenWeatherMapService {
        return Retrofit.Builder()
                .baseUrl(OPENWEATHERMAP_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create()
    }

    @Provides
    @Reusable
    fun provideVisibilityProvider(
            context: Context,
            openWeatherMapService: OpenWeatherMapService
    ): VisibilityProvider {
        val apiKey = context.getString(R.string.api_key_openweathermap)
        return RetrofittedOpenWeatherMapVisibilityProvider(openWeatherMapService, apiKey)
    }

    @Provides
    @Reusable
    fun provideKpIndexService(): KpIndexService {
        return Retrofit.Builder()
                .baseUrl(GEOMAG_ACTIVITY_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create()
    }

    @Provides
    @Singleton
    @Named(LAST_NOTIFIED_NAME)
    fun provideLastNotifiedAuroraReportCache(
            context: Context
    ): SingletonCache<AuroraReport> = DualSingletonCache(LAST_NOTIFIED_CACHE_ID, AuroraReport.default, auroraReportCacheSerializer, context)

    companion object {
        private val LAST_NOTIFIED_CACHE_ID = "last-notified-aurora-report"
        private val OPENWEATHERMAP_API_URL = "http://api.openweathermap.org/data/2.5/"
        private val GEOMAG_ACTIVITY_API_URL = "http://skylight-app.net/rest/"
    }
}
