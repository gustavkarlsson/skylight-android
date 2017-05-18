package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class SunPosition {
	Float zenithAngle;

	SunPosition() {
	}

	public SunPosition(Float zenithAngle) {
		this.zenithAngle = zenithAngle;
	}

	public Float getZenithAngle() {
		return zenithAngle;
	}

	@Override
	public String toString() {
		return "SunPosition{" +
				"zenithAngle=" + zenithAngle +
				'}';
	}
}
