package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import net.e175.klaus.solarpositioning.AzimuthZenithAngle;
import net.e175.klaus.solarpositioning.Grena3;

import java.util.GregorianCalendar;

import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class KlausBrunnerSunPositionProvider implements SunPositionProvider {
	@Override
	public Timestamped<Float> getZenithAngle(long timeMillis, double latitude, double longitude) throws ProviderException {
		try {
			GregorianCalendar date = new GregorianCalendar();
			date.setTimeInMillis(timeMillis);
			AzimuthZenithAngle azimuthZenithAngle = Grena3.calculateSolarPosition(date, latitude, longitude, 0);
			return new Timestamped<>((float) azimuthZenithAngle.getZenithAngle());
		} catch (Exception e) {
			throw new ProviderException("Failed to get zenith angle", e);
		}
	}
}
