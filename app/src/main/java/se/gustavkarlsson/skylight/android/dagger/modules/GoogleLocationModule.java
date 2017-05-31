package se.gustavkarlsson.skylight.android.dagger.modules;

import android.content.Context;
import android.location.Geocoder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.GeocoderAsyncAddressProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.GoogleLocationProvider;

@Module
public abstract class GoogleLocationModule {

	@Binds
	@Reusable
	abstract LocationProvider bindLocationProvider(GoogleLocationProvider googleLocationProvider);

	@Provides
	@Reusable
	static GoogleApiClient provideGoogleApiClient(Context context) {
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

}
