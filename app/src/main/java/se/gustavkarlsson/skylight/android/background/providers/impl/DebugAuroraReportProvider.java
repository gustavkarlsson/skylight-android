package se.gustavkarlsson.skylight.android.background.providers.impl;

import android.location.Address;
import android.support.annotation.NonNull;

import java.util.Locale;

import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;
import se.gustavkarlsson.skylight.android.settings.DebugSettings;

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
		GeomagLocation geomagLocation = new GeomagLocation(debugSettings.getGeomagLatitude());
		Darkness darkness = new Darkness(debugSettings.getSunZenithAngle());
		Visibility visibility = new Visibility(debugSettings.getCloudPercentage());
		return new AuroraFactors(
				geomagActivity,
				geomagLocation,
				darkness,
				visibility);
	}
}
