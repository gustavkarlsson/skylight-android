package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;

public class SunPositionViewModel extends BaseObservable {
	private final RealmSunPosition realmSunPosition;

	public SunPositionViewModel(RealmSunPosition realmSunPosition) {
		this.realmSunPosition = realmSunPosition;
	}

	@Bindable
	public String getZenithAngle() {
		return realmSunPosition.getZenithAngle() == null ? "-" : "" + realmSunPosition.getZenithAngle() + "° @ " + realmSunPosition.getTimestamp();
	}
}
