package se.gustavkarlsson.aurora_notifier.android.gui.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.BR;

public class KpIndexModel extends BaseObservable {

	private Float value;
	private long timestamp;

	@Bindable
	public String getValue() {
		return value == null ? "-" : String.valueOf(value);
	}

	public void setValue(Float value) {
		this.value = value;
		this.notifyPropertyChanged(BR.value);
	}

	@Bindable
	public String getTimestamp() {
		return String.valueOf(timestamp);
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		this.notifyPropertyChanged(BR.timestamp);
	}
}
