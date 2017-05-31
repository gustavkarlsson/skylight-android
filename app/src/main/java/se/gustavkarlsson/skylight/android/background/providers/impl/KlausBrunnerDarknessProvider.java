package se.gustavkarlsson.skylight.android.background.providers.impl;

import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.Grena3;

import java.util.GregorianCalendar;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.background.providers.DarknessProvider;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;

public class KlausBrunnerDarknessProvider implements DarknessProvider {

	@Inject
	KlausBrunnerDarknessProvider() {
	}

	@Override
	public Darkness getDarkness(long timeMillis, double latitude, double longitude) {
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(timeMillis);
		AzimuthZenithAngle azimuthZenithAngle = Grena3.calculateSolarPosition(date, latitude, longitude, 0);
		return new Darkness((float) azimuthZenithAngle.getZenithAngle());
	}
}
