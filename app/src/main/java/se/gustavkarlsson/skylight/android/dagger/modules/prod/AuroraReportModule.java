package se.gustavkarlsson.skylight.android.dagger.modules.prod;

import android.content.Context;
import android.location.Geocoder;
import android.net.ConnectivityManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.threeten.bp.Clock;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;
import se.gustavkarlsson.skylight.android.BuildConfig;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.skylight.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider;
import se.gustavkarlsson.skylight.android.background.providers.VisibilityProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.AuroraReportProviderImpl;
import se.gustavkarlsson.skylight.android.background.providers.impl.DebugAuroraReportProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.GeocoderAsyncAddressProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.GoogleLocationProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.RetrofittedGeomagActivityProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors.AsyncAuroraFactorsProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap.OpenWeatherMapService;
import se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap.RetrofittedOpenWeatherMapVisibilityProvider;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.DarknessModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.GeomagLocationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.SystemServiceModule;
import se.gustavkarlsson.skylight.android.settings.DebugSettings;

@Module(includes = {
		GeomagLocationModule.class,
		DarknessModule.class,
		SystemServiceModule.class,
		ZoneIdModule.class
})
public abstract class AuroraReportModule {
	private static final String OPENWEATHERMAP_API_URL = "http://api.openweathermap.org/data/2.5/";
	private static final String GEOMAG_ACTIVITY_API_URL = "http://skylight-app.net/rest/";

	// Published
	@Provides
	static AuroraReportProvider provideAuroraReportProvider(DebugSettings debugSettings, ConnectivityManager connectivityManager, LocationProvider locationProvider, AuroraFactorsProvider auroraFactorsProvider, AsyncAddressProvider asyncAddressProvider, Clock clock) {
		if (BuildConfig.DEBUG && debugSettings.isOverrideValues()) {
			return new DebugAuroraReportProvider(debugSettings, clock);
		} else {
			return new AuroraReportProviderImpl(connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock);
		}
	}

	@Binds
	@Reusable
	abstract LocationProvider bindLocationProvider(GoogleLocationProvider googleLocationProvider);

	@Binds
	@Reusable
	abstract AuroraFactorsProvider bindAuroraFactorsProvider(AsyncAuroraFactorsProvider provider);

	@Provides
	@Reusable
	static OpenWeatherMapService provideOpenWeatherMapService() {
		return new Retrofit.Builder()
				.baseUrl(OPENWEATHERMAP_API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
	}

	@Provides
	@Reusable
	static VisibilityProvider provideVisibilityProvider(Context context, OpenWeatherMapService openWeatherMapService) {
		String apiKey = context.getString(R.string.api_key_openweathermap);
		return new RetrofittedOpenWeatherMapVisibilityProvider(openWeatherMapService, apiKey);
	}

	@Provides
	@Reusable
	static GoogleApiClient provideGoogleApiLocationClient(Context context) {
		return new GoogleApiClient.Builder(context)
				.addApi(LocationServices.API)
				.build();
	}

	@Provides
	@Reusable
	static Geocoder provideGeocoder(Context context) {
		return new Geocoder(context);
	}

	@Binds
	@Reusable
	abstract AsyncAddressProvider bindAsyncAddressProvider(GeocoderAsyncAddressProvider provider);

	@Provides
	@Reusable
	static KpIndexService provideKpIndexService() {
		return new Retrofit.Builder()
				.baseUrl(GEOMAG_ACTIVITY_API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(KpIndexService.class);
	}

	@Binds
	@Reusable
	abstract GeomagActivityProvider bindGeomagActivityProvider(RetrofittedGeomagActivityProvider provider);

}
