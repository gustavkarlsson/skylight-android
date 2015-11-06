package se.gustavkarlsson.aurora_notifier.android.parcels;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class AuroraUpdate {

	Timestamped<Float> kpIndex;

	public Timestamped<Float> getKpIndex() {
		return kpIndex;
	}

	public void setKpIndex(Timestamped<Float> kpIndex) {
		this.kpIndex = kpIndex;
	}
}
