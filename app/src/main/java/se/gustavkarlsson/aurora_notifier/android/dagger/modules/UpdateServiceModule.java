package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.AuroraDataProviderImpl;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GeomagneticLocationProviderImpl;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GoogleLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.KlausBrunnerSunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.OpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedSolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

@Module
public class UpdateServiceModule {
	public static final String NAME_UPDATE_TIMEOUT_MILLIS = "updateTimeoutMillis";

	private final UpdateService updateService;

	public UpdateServiceModule(UpdateService updateService) {
		this.updateService = updateService;
	}

	@Provides
	@Reusable
	SunPositionProvider provideSunPositionProvider() {
		return new KlausBrunnerSunPositionProvider();
	}

	@Provides
	@Reusable
	GeomagneticLocationProvider provideGeomagneticLocationProvider() {
		return new GeomagneticLocationProviderImpl();
	}

	@Provides
	@Reusable
	GoogleApiClient provideGoogleApiClient() {
		return new GoogleApiClient.Builder(updateService)
				.addApi(LocationServices.API)
				.build();
	}

	@Provides
	@Reusable
	LocalBroadcastManager provideLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(updateService);
	}

	@Provides
	@Singleton
	OpenWeatherMapService provideOpenWeatherMapService() {
		return new Retrofit.Builder()
				.baseUrl("http://api.openweathermap.org/data/2.5/")
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
	}

	@Provides
	@Reusable
	WeatherProvider provideWeatherProvider(OpenWeatherMapService openWeatherMapService) {
		// TODO Look into getting a new API key
		return new RetrofittedOpenWeatherMapProvider(openWeatherMapService, "317cc1cbab742dfda3c96c93e7873b6e");
	}

	@Provides
	@Reusable
	KpIndexService provideKpIndexService() {
		return new Retrofit.Builder()
				// TODO Update to more permanent hostname
				.baseUrl("http://9698.s.t4vps.eu/rest/")
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(KpIndexService.class);
	}

	@Provides
	@Reusable
	SolarActivityProvider provideSolarActivityProvider(KpIndexService kpIndexService) {
		return new RetrofittedSolarActivityProvider(kpIndexService);
	}

	@Provides
	@Reusable
	LocationProvider provideLocationProvider(GoogleApiClient googleApiClient) {
		return new GoogleLocationProvider(googleApiClient);
	}

	@Provides
	@Reusable
	AuroraDataProvider provideAuroraDataProvider(
			SolarActivityProvider solarActivityProvider,
			WeatherProvider weatherProvider,
			SunPositionProvider sunPositionProvider,
			GeomagneticLocationProvider geomagneticLocationProvider) {
		return new AuroraDataProviderImpl(solarActivityProvider,
				weatherProvider,
				sunPositionProvider,geomagneticLocationProvider);
	}

	@Provides
	@Named(NAME_UPDATE_TIMEOUT_MILLIS)
	long provideTimeoutMillis() {
		return 60_000;
	}

}
