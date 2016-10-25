package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticCoordinatesProvider;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

import static java.lang.Math.*;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

public class GeomagneticCoordinatesProviderImpl implements GeomagneticCoordinatesProvider {
	// 2015 data
	private static final double MAGNETIC_NORTH_POLE_LATITUDE = 80.4;
	private static final double MAGNETIC_NORTH_POLE_LONGITUDE = -72.6;
	private static final double MAGNETIC_SOUTH_POLE_LATITUDE = -80.4;
	private static final double MAGNETIC_SOUTH_POLE_LONGITUDE = 107.4;

	@Override
	public Timestamped<Float> getDegreesFromClosestPole(double longitude, double latitude) {
		double degreesFromNorthPole = calculateDistanceDegrees(latitude, longitude, MAGNETIC_NORTH_POLE_LATITUDE, MAGNETIC_NORTH_POLE_LONGITUDE);
		double degreesFromSouthPole = calculateDistanceDegrees(latitude, longitude, MAGNETIC_SOUTH_POLE_LATITUDE, MAGNETIC_SOUTH_POLE_LONGITUDE);
		double degreesFromNearestPole = min(degreesFromNorthPole, degreesFromSouthPole);
		return new Timestamped<>((float) degreesFromNearestPole);
	}

	private static double calculateDistanceDegrees(double latitude1, double longitude1, double latitude2, double longitude2) {
		double dLat = toRadians(latitude2 - latitude1);
		double dLon = toRadians(longitude2 - longitude1);
		double a = sin(dLat / 2) * sin(dLat / 2)
				+ cos(toRadians(latitude1)) * cos(toRadians(latitude2))
				* sin(dLon / 2) * sin(dLon / 2);
		double distanceRads = abs(atan2(sqrt(a), sqrt(1 - a)));
		return toDegrees(distanceRads);
	}
}
