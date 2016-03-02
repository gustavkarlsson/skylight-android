package se.gustavkarlsson.aurora_notifier.android.realm.place;

import io.realm.RealmObject;
import se.gustavkarlsson.aurora_notifier.android.domain.place.Position;

public class RealmPosition extends RealmObject implements Position {

	private double latitude;

	private double longitude;

	@Override
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
