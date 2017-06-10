package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.providers.*
import se.gustavkarlsson.skylight.android.background.providers.impl.*
import se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors.AsyncAuroraFactorsProvider
import se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors.ErrorHandlingExecutorService
import se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap.OpenWeatherMapService
import se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap.RetrofittedOpenWeatherMapVisibilityProvider
import se.gustavkarlsson.skylight.android.cache.DualAuroraReportSingletonCache
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME
import se.gustavkarlsson.skylight.android.dagger.Names.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ContextModule
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.DarknessModule
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.GeomagLocationModule
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.SystemServiceModule
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.settings.DebugSettings
import se.gustavkarlsson.skylight.android.settings.Settings
import se.gustavkarlsson.skylight.android.settings.SharedPreferencesDebugSettings
import se.gustavkarlsson.skylight.android.settings.SharedPreferencesSettings
import se.gustavkarlsson.skylight.android.util.ZoneIdProvider
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = arrayOf(
		ContextModule::class,
		GeomagLocationModule::class,
		DarknessModule::class,
		SystemServiceModule::class
))
class AuroraReportModule {

	@Provides
	@Reusable
	fun provideLocationProvider(googleApiClient: GoogleApiClient): LocationProvider {
		return GoogleLocationProvider(googleApiClient)
	}

	@Provides
	@Reusable
	fun provideAuroraFactorsProvider(
			geomagActivityProvider: RetrofittedGeomagActivityProvider,
			visibilityProvider: VisibilityProvider,
			darknessProvider: KlausBrunnerDarknessProvider,
			geomagLocProvider: GeomagLocationProviderImpl,
			executorService: ErrorHandlingExecutorService,
			clock: Clock
	): AuroraFactorsProvider {
		return AsyncAuroraFactorsProvider(geomagActivityProvider, visibilityProvider, darknessProvider, geomagLocProvider, executorService, clock)
	}

	@Provides
	@Reusable
	fun provideAsyncAddressProvider(geocoder: Geocoder, @Named(CACHED_THREAD_POOL_NAME) cachedThreadPool: ExecutorService): AsyncAddressProvider {
		return GeocoderAsyncAddressProvider(geocoder, cachedThreadPool)
	}

	@Provides
	@Reusable
	fun provideGeomagActivityProvider(kpIndexService: KpIndexService): GeomagActivityProvider {
		return RetrofittedGeomagActivityProvider(kpIndexService)
	}

	@Provides
	@Reusable
	fun provideSettings(context: Context): Settings {
		return SharedPreferencesSettings(context)
	}

	@Provides
	@Reusable
	fun provideDebugSettings(context: Context): DebugSettings {
		return SharedPreferencesDebugSettings(context)
	}

	// Published
	@Provides
	fun provideAuroraReportProvider(
			debugSettings: DebugSettings,
			connectivityManager: ConnectivityManager,
			locationProvider: LocationProvider,
			auroraFactorsProvider: AuroraFactorsProvider,
			asyncAddressProvider: AsyncAddressProvider,
			clock: Clock
	): AuroraReportProvider {
		if (debugSettings.isOverrideValues) {
			return DebugAuroraReportProvider(debugSettings, clock)
		} else {
			return AuroraReportProviderImpl(connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock)
		}
	}

	@Provides
	@Reusable
	fun provideOpenWeatherMapService(): OpenWeatherMapService {
		return Retrofit.Builder()
				.baseUrl(OPENWEATHERMAP_API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(OpenWeatherMapService::class.java)
	}

	@Provides
	@Reusable
	fun provideVisibilityProvider(context: Context, openWeatherMapService: OpenWeatherMapService): VisibilityProvider {
		val apiKey = context.getString(R.string.api_key_openweathermap)
		return RetrofittedOpenWeatherMapVisibilityProvider(openWeatherMapService, apiKey)
	}

	@Provides
	@Reusable
	fun provideGoogleApiLocationClient(context: Context): GoogleApiClient {
		return GoogleApiClient.Builder(context)
				.addApi(LocationServices.API)
				.build()
	}

	@Provides
	@Reusable
	fun provideGeocoder(context: Context): Geocoder {
		return Geocoder(context)
	}

	@Provides
	@Reusable
	fun provideKpIndexService(): KpIndexService {
		return Retrofit.Builder()
				.baseUrl(GEOMAG_ACTIVITY_API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(KpIndexService::class.java)
	}

	@Provides
	fun provideZoneIdSupplier(): ZoneIdProvider {
		return object : ZoneIdProvider {
			override val zoneId: ZoneId
				get() = ZoneOffset.systemDefault()
		}
	}

	@Provides
	@Singleton
	@Named(LAST_NOTIFIED_NAME)
	fun provideLastNotifiedAuroraReportCache(context: Context): SingletonCache<AuroraReport> {
		return DualAuroraReportSingletonCache(context, LAST_NOTIFIED_CACHE_ID)
	}

	@Provides
	@Singleton
	@Named(CACHED_THREAD_POOL_NAME)
	fun provideCachedThreadPool(): ExecutorService {
		return Executors.newCachedThreadPool()
	}

	companion object {
		private val LAST_NOTIFIED_CACHE_ID = "last-notified-aurora-report"
		private val OPENWEATHERMAP_API_URL = "http://api.openweathermap.org/data/2.5/"
		private val GEOMAG_ACTIVITY_API_URL = "http://skylight-app.net/rest/"
	}

}
