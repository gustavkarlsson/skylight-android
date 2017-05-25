package se.gustavkarlsson.aurora_notifier.android.models.factors;

import org.parceler.Parcel;

@Parcel
public class GeomagActivity {
	Float kpIndex;

	public GeomagActivity() {
	}

	public GeomagActivity(Float kpIndex) {
		this.kpIndex = kpIndex;
	}

	public Float getKpIndex() {
		return kpIndex;
	}

	@Override
	public String toString() {
		return "GeomagActivity{" +
				"kpIndex=" + kpIndex +
				'}';
	}
}
