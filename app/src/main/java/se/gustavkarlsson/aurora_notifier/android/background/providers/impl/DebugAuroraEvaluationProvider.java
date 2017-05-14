package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.support.annotation.NonNull;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;
import se.gustavkarlsson.aurora_notifier.android.settings.DebugSettings;

public class DebugAuroraEvaluationProvider implements AuroraEvaluationProvider {

	private final DebugSettings debugSettings;

	public DebugAuroraEvaluationProvider(DebugSettings debugSettings) {
		this.debugSettings = debugSettings;
	}

	@Override
	public AuroraEvaluation getEvaluation(long timeoutMillis) {
		Address location = new Address(Locale.ENGLISH);
		AuroraFactors auroraFactors = createAuroraFactors();
		return new AuroraEvaluation(System.currentTimeMillis(), location, auroraFactors);
	}

	@NonNull
	private AuroraFactors createAuroraFactors() {
		SolarActivity solarActivity = new SolarActivity(debugSettings.getKpIndex());
		GeomagneticLocation geomagneticLocation = new GeomagneticLocation(debugSettings.getDegreesFromClosestPole());
		SunPosition sunPosition = new SunPosition(debugSettings.getSunZenithAngle());
		Weather weather = new Weather(debugSettings.getCloudPercentage());
		return new AuroraFactors(
				solarActivity,
				geomagneticLocation,
				sunPosition,
				weather);
	}
}
