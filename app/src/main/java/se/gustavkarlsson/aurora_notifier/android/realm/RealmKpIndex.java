package se.gustavkarlsson.aurora_notifier.android.realm;

import io.realm.RealmObject;

public class RealmKpIndex extends RealmObject {
	private Float kpIndex;
	private Long timestamp;

	public Float getKpIndex() {
		return kpIndex;
	}

	public void setKpIndex(Float kpIndex) {
		this.kpIndex = kpIndex;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "RealmKpIndex{" +
				"kpIndex=" + getKpIndex() +
				", timestamp=" + getTimestamp() +
				'}' ;
	}
}
