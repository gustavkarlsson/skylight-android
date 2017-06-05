package se.gustavkarlsson.skylight.android.background.providers.impl;

import android.location.Address;
import android.support.annotation.NonNull;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;

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
	private final Clock clock;

	public DebugAuroraReportProvider(DebugSettings debugSettings, Clock clock) {
		this.debugSettings = debugSettings;
		this.clock = clock;
	}

	@Override
	public AuroraReport getReport(Duration timeout) {
		Address location = new Address(Locale.ENGLISH);
		AuroraFactors auroraFactors = createAuroraFactors();
		return new AuroraReport(clock.millis(), location, auroraFactors);
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
