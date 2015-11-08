package se.gustavkarlsson.aurora_notifier.android.parcels;

import org.parceler.Parcel;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

@Parcel
public class AuroraUpdate {

	public static final String TAG = AuroraUpdate.class.getSimpleName();

	Float kpIndex;
	long kpIndexTimestamp;

	public AuroraUpdate() {
		// Required empty constructor
	}

	public AuroraUpdate(Timestamped<Float> kpIndex) {
		this.kpIndex = kpIndex.getValue();
		this.kpIndexTimestamp = kpIndex.getTimestamp();
	}

	public Float getKpIndex() {
		return kpIndex;
	}

	public void setKpIndex(Float kpIndex) {
		this.kpIndex = kpIndex;
	}

	public long getKpIndexTimestamp() {
		return kpIndexTimestamp;
	}

	public void setKpIndexTimestamp(long kpIndexTimestamp) {
		this.kpIndexTimestamp = kpIndexTimestamp;
	}
}
