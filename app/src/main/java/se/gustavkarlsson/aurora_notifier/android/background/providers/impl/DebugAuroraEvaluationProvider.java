package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraDataComplicationsEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;
import se.gustavkarlsson.aurora_notifier.android.settings.DebugSettings;

public class DebugAuroraEvaluationProvider implements AuroraEvaluationProvider {

	private final DebugSettings debugSettings;

	public DebugAuroraEvaluationProvider(DebugSettings debugSettings) {
		this.debugSettings = debugSettings;
	}

	@Override
	public AuroraEvaluation getEvaluation(long timeoutMillis) {
		Address location = new Address(Locale.ENGLISH);
		AuroraData auroraData = createAuroraData();
		List<AuroraComplication> complications = new AuroraDataComplicationsEvaluator(auroraData).evaluate();
		return new AuroraEvaluation(System.currentTimeMillis(), location, auroraData, complications);
	}

	@NonNull
	private AuroraData createAuroraData() {
		SolarActivity solarActivity = new SolarActivity(debugSettings.getKpIndex());
		GeomagneticLocation geomagneticLocation = new GeomagneticLocation(debugSettings.getDegreesFromClosestPole());
		SunPosition sunPosition = new SunPosition(debugSettings.getSunZenithAngle());
		Weather weather = new Weather(debugSettings.getCloudPercentage());
		return new AuroraData(
				solarActivity,
				geomagneticLocation,
				sunPosition,
				weather);
	}
}
