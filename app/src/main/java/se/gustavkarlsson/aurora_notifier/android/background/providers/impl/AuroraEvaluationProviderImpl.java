package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

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

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraFactorsProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.util.CountdownTimer;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AuroraEvaluationProviderImpl implements AuroraEvaluationProvider {
	private static final String TAG = AuroraEvaluationProviderImpl.class.getSimpleName();

	private final ConnectivityManager connectivityManager;
	private final LocationProvider locationProvider;
	private final AuroraFactorsProvider auroraFactorsProvider;
	private final AsyncAddressProvider asyncAddressProvider;

	public AuroraEvaluationProviderImpl(ConnectivityManager connectivityManager, LocationProvider locationProvider, AuroraFactorsProvider auroraFactorsProvider, AsyncAddressProvider asyncAddressProvider) {
		this.connectivityManager = connectivityManager;
		this.locationProvider = locationProvider;
		this.auroraFactorsProvider = auroraFactorsProvider;
		this.asyncAddressProvider = asyncAddressProvider;
	}

	@Override
	public AuroraEvaluation getEvaluation(long timeoutMillis) {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			throw new UserFriendlyException(R.string.error_no_internet);
		}

		CountdownTimer timeoutTimer = CountdownTimer.start(timeoutMillis);
		Location location = locationProvider.getLocation(timeoutTimer.getRemainingTimeMillis());
		Future<Address> addressFuture = asyncAddressProvider.execute(location.getLatitude(), location.getLongitude());
		AuroraFactors auroraFactors = auroraFactorsProvider.getAuroraFactors(location, timeoutTimer.getRemainingTimeMillis());
		Address address = getAddressOrNull(addressFuture, timeoutTimer.getRemainingTimeMillis());
		return new AuroraEvaluation(System.currentTimeMillis(), address, auroraFactors);
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
