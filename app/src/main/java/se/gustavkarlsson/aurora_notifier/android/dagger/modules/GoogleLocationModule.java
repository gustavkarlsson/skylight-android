package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;
import android.location.Geocoder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GeocoderAsyncAddressProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GoogleLocationProvider;

@Module
public abstract class GoogleLocationModule {

	@Provides
	@Reusable
	static LocationProvider provideLocationProvider(Context context) {
		GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
				.addApi(LocationServices.API)
				.build();
		return new GoogleLocationProvider(googleApiClient);
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
