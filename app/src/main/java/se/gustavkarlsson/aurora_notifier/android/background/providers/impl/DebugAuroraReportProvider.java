package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.location.Address;
import android.support.annotation.NonNull;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;
import se.gustavkarlsson.aurora_notifier.android.settings.DebugSettings;

public class DebugAuroraReportProvider implements AuroraReportProvider {

	private final DebugSettings debugSettings;

	public DebugAuroraReportProvider(DebugSettings debugSettings) {
		this.debugSettings = debugSettings;
	}

	@Override
	public AuroraReport getReport(long timeoutMillis) {
		Address location = new Address(Locale.ENGLISH);
		AuroraFactors auroraFactors = createAuroraFactors();
		return new AuroraReport(System.currentTimeMillis(), location, auroraFactors);
	}

	@NonNull
	private AuroraFactors createAuroraFactors() {
		GeomagActivity geomagActivity = new GeomagActivity(debugSettings.getKpIndex());
		GeomagLocation geomagLocation = new GeomagLocation(debugSettings.getDegreesFromClosestPole());
		Darkness darkness = new Darkness(debugSettings.getSunZenithAngle());
		Weather weather = new Weather(debugSettings.getCloudPercentage());
		return new AuroraFactors(
				geomagActivity,
				geomagLocation,
				darkness,
				weather);
	}
}
