package se.gustavkarlsson.aurora_notifier.android.realm;

import io.realm.RealmObject;

public class RealmGeomagneticCoordinates extends RealmObject {
	private Float degreesFromClosestPole;
	private Long timestamp;

	public Float getDegreesFromClosestPole() {
		return degreesFromClosestPole;
	}

	public void setDegreesFromClosestPole(Float degreesFromClosestPole) {
		this.degreesFromClosestPole = degreesFromClosestPole;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "RealmGeomagneticCoordinates{" +
				"degreesFromClosestPole=" + getDegreesFromClosestPole() +
				", timestamp=" + getTimestamp() +
				'}' ;
	}
}
