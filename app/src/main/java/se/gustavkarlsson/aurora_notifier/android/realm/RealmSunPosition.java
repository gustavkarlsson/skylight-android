package se.gustavkarlsson.aurora_notifier.android.realm;

import io.realm.RealmObject;

public class RealmSunPosition extends RealmObject {
	private Float zenithAngle;
	private Long timestamp;

	public Float getZenithAngle() {
		return zenithAngle;
	}

	public void setZenithAngle(Float zenithAngle) {
		this.zenithAngle = zenithAngle;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "RealmSunPosition{" +
				"zenithAngle=" + getZenithAngle() +
				", timestamp=" + getTimestamp() +
				'}' ;
	}
}
