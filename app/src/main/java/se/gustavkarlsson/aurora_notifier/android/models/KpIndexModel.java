package se.gustavkarlsson.aurora_notifier.android.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.BR;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class KpIndexModel extends BaseObservable {

	private Timestamped<Float> kpIndex;

	public void setKpIndex(Timestamped<Float> kpIndex) {
		this.kpIndex = kpIndex;
		notifyPropertyChanged(BR._all);
	}

	@Bindable
	public String getValue() {
		return kpIndex == null ? "-" : String.valueOf(kpIndex.getValue());
	}

	@Bindable
	public String getTimestamp() {
		return kpIndex == null ? "-" : String.valueOf(kpIndex.getTimestamp());
	}
}
