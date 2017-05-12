package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AsyncAddressProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraDataComplicationsEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.util.CountdownTimer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AuroraEvaluationProviderImpl implements AuroraEvaluationProvider {
	private static final String TAG = AuroraEvaluationProviderImpl.class.getSimpleName();

	private final LocationProvider locationProvider;
	private final AuroraDataProvider auroraDataProvider;
	private final AsyncAddressProvider asyncAddressProvider;

	@Inject
	AuroraEvaluationProviderImpl(LocationProvider locationProvider, AuroraDataProvider auroraDataProvider, AsyncAddressProvider asyncAddressProvider) {
		this.locationProvider = locationProvider;
		this.auroraDataProvider = auroraDataProvider;
		this.asyncAddressProvider = asyncAddressProvider;
	}

	@Override
	public AuroraEvaluation getEvaluation(long timeoutMillis) {
		CountdownTimer timeoutTimer = CountdownTimer.start(timeoutMillis);
		Location location = locationProvider.getLocation(timeoutTimer.getRemainingTimeMillis());
		Future<Address> addressFuture = asyncAddressProvider.execute(location.getLatitude(), location.getLongitude());
		AuroraData auroraData = auroraDataProvider.getAuroraData(location, timeoutTimer.getRemainingTimeMillis());
		Address address = getAddressOrNull(addressFuture, timeoutTimer.getRemainingTimeMillis());
		List<AuroraComplication> complications = new AuroraDataComplicationsEvaluator(auroraData).evaluate();
		return new AuroraEvaluation(System.currentTimeMillis(), address, auroraData, complications);
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
