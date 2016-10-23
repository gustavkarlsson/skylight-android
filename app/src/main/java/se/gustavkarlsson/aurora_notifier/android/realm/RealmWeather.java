package se.gustavkarlsson.aurora_notifier.android.realm;

import io.realm.RealmObject;

public class RealmWeather extends RealmObject {
	private Integer cloudPercentage;
	private Long timestamp;

	public Integer getCloudPercentage() {
		return cloudPercentage;
	}

	public void setCloudPercentage(Integer cloudPercentage) {
		this.cloudPercentage = cloudPercentage;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
