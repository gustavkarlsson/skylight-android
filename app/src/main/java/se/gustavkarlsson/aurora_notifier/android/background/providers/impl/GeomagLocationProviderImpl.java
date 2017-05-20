package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class GeomagLocationProviderImpl implements GeomagLocationProvider {

	// 2015 data
	private static final double MAGNETIC_NORTH_POLE_LATITUDE = 80.4;
	private static final double MAGNETIC_NORTH_POLE_LONGITUDE = -72.6;

	@Inject
	GeomagLocationProviderImpl() {
	}

	@Override
	public GeomagLocation getGeomagLocation(double latitude, double longitude) {
		double geomagneticLatitude = calculateGeomagneticLatitude(latitude, longitude, MAGNETIC_NORTH_POLE_LATITUDE, MAGNETIC_NORTH_POLE_LONGITUDE);
		return new GeomagLocation((float) geomagneticLatitude);
	}

	// http://stackoverflow.com/a/7949249/940731
	private static double calculateGeomagneticLatitude(double latitude1, double longitude1, double latitude2, double longitude2) {
		double Dlong = longitude2;
		double Dlat = latitude2;
		double R = 1;

		//convert first to radians
		Dlong = toRadians(Dlong);
		Dlat = toRadians(Dlat);

		double glat = toRadians(latitude1);
		double glon = toRadians(longitude1);
		double galt = glat * 0. + R;

		double[] coord = new double[]{glat, glon, galt};

		//convert to rectangular coordinates
		//X-axis: defined by the vector going from Earth's center towards the intersection of the equator and Greenwitch's meridian.
		//Z-axis: axis of the geographic poles
		//Y-axis: defined by Y=Z^X
		double x = coord[2] * cos(coord[0]) * cos(coord[1]);
		double y = coord[2] * cos(coord[0]) * sin(coord[1]);
		double z = coord[2] * sin(coord[0]);
		double[] xyz = new double[]{x, y, z};

		//Compute 1st rotation matrix : rotation around plane of the equator,
		//from the Greenwich meridian to the meridian containing the magnetic
		//dipole pole.
		double[][] geolong2maglong = new double[3][3];
		geolong2maglong[0][0] = cos(Dlong);
		geolong2maglong[0][1] = sin(Dlong);
		geolong2maglong[1][0] = -sin(Dlong);
		geolong2maglong[1][1] = cos(Dlong);
		geolong2maglong[2][2] = 1;
		double[] out = dotProduct(geolong2maglong, xyz);


		//Second rotation : in the plane of the current meridian from geographic pole to magnetic dipole pole.
		double[][] tomaglat = new double[3][3];
		tomaglat[0][0] = cos((PI / 2) - Dlat);
		tomaglat[0][2] = -sin((PI / 2) - Dlat);
		tomaglat[2][0] = sin((PI / 2) - Dlat);
		tomaglat[2][2] = cos((PI / 2) - Dlat);
		tomaglat[1][1] = 1;
		out = dotProduct(tomaglat, out);

		double mlat = atan2(out[2], sqrt(pow(out[0], 2) + pow(out[1], 2)));
		mlat = toDegrees(mlat);
		return mlat;
	}

	private static double[] dotProduct(double[][] a, double[] b) {
		int n = b.length;
		double[] c = new double[n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				c[i] += a[i][j] * b[j];
			}
		}
		return c;
	}
}
