package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;

public class GeomagneticCoordinatesViewModel extends RealmViewModel<RealmGeomagneticCoordinates> {
	public GeomagneticCoordinatesViewModel(RealmGeomagneticCoordinates value) {
		super(value);
	}

	@Bindable
	public String getDegreesFromClosestPole() {
		return getValue().getDegreesFromClosestPole() == null ? "-" : "" + getValue().getDegreesFromClosestPole() + "° @ " + getValue().getTimestamp();
	}
}
