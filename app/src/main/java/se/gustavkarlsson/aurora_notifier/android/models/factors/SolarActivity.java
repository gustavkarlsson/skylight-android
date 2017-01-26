package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class SolarActivity {
	float kpIndex;

	SolarActivity() {
	}

	public SolarActivity(float kpIndex) {
		this.kpIndex = kpIndex;
	}

	public float getKpIndex() {
		return kpIndex;
	}

	@Override
	public String toString() {
		return "SolarActivity{" +
				"kpIndex=" + kpIndex +
				'}';
	}
}
