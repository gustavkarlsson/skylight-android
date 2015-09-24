package se.gustavkarlsson.aurora_notifier.android.domain.place;

import io.realm.RealmObject;

public class Position extends RealmObject {

	private double latitude;
	private double longitude;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Position position = (Position) o;

		if (Double.compare(position.latitude, latitude) != 0) return false;
		return Double.compare(position.longitude, longitude) == 0;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "Position{" +
				"latitude=" + latitude +
				", longitude=" + longitude +
				'}';
	}
}
