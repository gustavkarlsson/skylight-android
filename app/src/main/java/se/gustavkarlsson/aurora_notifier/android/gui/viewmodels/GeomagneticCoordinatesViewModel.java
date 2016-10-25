package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;

public class GeomagneticCoordinatesViewModel extends BaseObservable {
	private final RealmGeomagneticCoordinates realmGeomagneticCoordinates;

	public GeomagneticCoordinatesViewModel(RealmGeomagneticCoordinates realmGeomagneticCoordinates) {
		this.realmGeomagneticCoordinates = realmGeomagneticCoordinates;
	}

	@Bindable
	public String getDegreesFromClosestPole() {
		return realmGeomagneticCoordinates.getDegreesFromClosestPole() == null ? "-" : "" + realmGeomagneticCoordinates.getDegreesFromClosestPole() + "° @ " + realmGeomagneticCoordinates.getTimestamp();
	}
}
