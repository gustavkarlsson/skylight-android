package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;

public class SunPositionViewModel extends RealmViewModel<RealmSunPosition> {
	public SunPositionViewModel(RealmSunPosition value) {
		super(value);
	}

	@Bindable
	public String getZenithAngle() {
		return getValue().getZenithAngle() == null ? "-" : "" + getValue().getZenithAngle() + "° @ " + getValue().getTimestamp();
	}
}
