package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class GeomagActivity {
	float kpIndex;

	GeomagActivity() {
	}

	public GeomagActivity(float kpIndex) {
		this.kpIndex = kpIndex;
	}

	public float getKpIndex() {
		return kpIndex;
	}

	@Override
	public String toString() {
		return "GeomagActivity{" +
				"kpIndex=" + kpIndex +
				'}';
	}
}
