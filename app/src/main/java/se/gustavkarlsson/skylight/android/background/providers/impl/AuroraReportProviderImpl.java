package se.gustavkarlsson.skylight.android.background.providers.impl;

import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.skylight.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.util.CountdownTimer;
import se.gustavkarlsson.skylight.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AuroraReportProviderImpl implements AuroraReportProvider {
	private static final String TAG = AuroraReportProviderImpl.class.getSimpleName();

	private final ConnectivityManager connectivityManager;
	private final LocationProvider locationProvider;
	private final AuroraFactorsProvider auroraFactorsProvider;
	private final AsyncAddressProvider asyncAddressProvider;

	public AuroraReportProviderImpl(ConnectivityManager connectivityManager, LocationProvider locationProvider, AuroraFactorsProvider auroraFactorsProvider, AsyncAddressProvider asyncAddressProvider) {
		this.connectivityManager = connectivityManager;
		this.locationProvider = locationProvider;
		this.auroraFactorsProvider = auroraFactorsProvider;
		this.asyncAddressProvider = asyncAddressProvider;
	}

	@Override
	public AuroraReport getReport(long timeoutMillis) {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			throw new UserFriendlyException(R.string.error_no_internet);
		}

		CountdownTimer timeoutTimer = CountdownTimer.start(timeoutMillis);
		Location location = locationProvider.getLocation(timeoutTimer.getRemainingTimeMillis());
		Future<Address> addressFuture = asyncAddressProvider.execute(location.getLatitude(), location.getLongitude());
		AuroraFactors auroraFactors = auroraFactorsProvider.getAuroraFactors(location, timeoutTimer.getRemainingTimeMillis());
		Address address = getAddressOrNull(addressFuture, timeoutTimer.getRemainingTimeMillis());
		return new AuroraReport(System.currentTimeMillis(), address, auroraFactors);
	}

	@Nullable
	private static Address getAddressOrNull(Future<Address> addressFuture, long timeoutMillis) {
		try {
			return addressFuture.get(timeoutMillis, MILLISECONDS);
		} catch (TimeoutException e) {
			Log.w(TAG, "Getting address timed out after " + timeoutMillis + "ms", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			Log.w(TAG, "An unexpected exception occurred while", cause);
		} catch (InterruptedException | CancellationException e) {
			Log.w(TAG, "An unexpected exception occurred", e);
		}
		return null;
	}
}
