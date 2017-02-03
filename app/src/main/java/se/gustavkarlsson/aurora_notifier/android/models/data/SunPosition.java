package se.gustavkarlsson.aurora_notifier.android.models.data;

import org.parceler.Parcel;

@Parcel
public class SunPosition {
	float zenithAngle;

	SunPosition() {
	}

	public SunPosition(float zenithAngle) {
		this.zenithAngle = zenithAngle;
	}

	public float getZenithAngle() {
		return zenithAngle;
	}

	@Override
	public String toString() {
		return "SunPosition{" +
				"zenithAngle=" + zenithAngle +
				'}';
	}
}
