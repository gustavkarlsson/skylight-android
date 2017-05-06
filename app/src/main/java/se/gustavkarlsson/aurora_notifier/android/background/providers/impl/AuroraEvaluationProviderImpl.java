package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.location.Location;

import java.util.List;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AddressProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraDataProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.LocationProvider;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraDataComplicationsEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.util.CountdownTimer;

public class AuroraEvaluationProviderImpl implements AuroraEvaluationProvider {
	private final LocationProvider locationProvider;
	private final AuroraDataProvider auroraDataProvider;
	private final AddressProvider addressProvider;

	@Inject
	AuroraEvaluationProviderImpl(LocationProvider locationProvider, AuroraDataProvider auroraDataProvider, AddressProvider addressProvider) {
		this.locationProvider = locationProvider;
		this.auroraDataProvider = auroraDataProvider;
		this.addressProvider = addressProvider;
	}

	@Override
	public AuroraEvaluation getEvaluation(long timeoutMillis) {
		CountdownTimer timeoutTimer = CountdownTimer.start(timeoutMillis);
		Location location = locationProvider.getLocation(timeoutTimer.getRemainingTimeMillis());
		Address address = addressProvider.getAddress(location.getLatitude(), location.getLongitude(), timeoutTimer.getRemainingTimeMillis());
		AuroraData auroraData = auroraDataProvider.getAuroraData(location, timeoutTimer.getRemainingTimeMillis());
		List<AuroraComplication> complications = new AuroraDataComplicationsEvaluator(auroraData).evaluate();
		return new AuroraEvaluation(System.currentTimeMillis(), address, auroraData, complications);
	}
}
