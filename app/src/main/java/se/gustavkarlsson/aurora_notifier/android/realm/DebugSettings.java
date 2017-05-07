package se.gustavkarlsson.aurora_notifier.android.realm;

import io.realm.Realm;
import io.realm.RealmObject;

// TODO replace with preferences
public class DebugSettings extends RealmObject {
	private boolean enabled;
	private float kpIndex;
	private float degreesFromGeomagneticPole;
	private float sunPosition;
	private int cloudPercentage;

	public static DebugSettings get(Realm realm) {
		DebugSettings singleton = realm.where(DebugSettings.class).findFirst();
		if (singleton == null) {
			realm.executeTransaction(r -> realm.createObject(DebugSettings.class));
			singleton = realm.where(DebugSettings.class).findFirst();
		}
		return singleton;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public float getKpIndex() {
		return kpIndex;
	}

	public void setKpIndex(float kpIndex) {
		this.kpIndex = kpIndex;
	}

	public float getDegreesFromGeomagneticPole() {
		return degreesFromGeomagneticPole;
	}

	public void setDegreesFromGeomagneticPole(float degreesFromGeomagneticPole) {
		this.degreesFromGeomagneticPole = degreesFromGeomagneticPole;
	}

	public float getSunPosition() {
		return sunPosition;
	}

	public void setSunPosition(float sunPosition) {
		this.sunPosition = sunPosition;
	}

	public int getCloudPercentage() {
		return cloudPercentage;
	}

	public void setCloudPercentage(int cloudPercentage) {
		this.cloudPercentage = cloudPercentage;
	}

	@Override
	public String toString() {
		return "DebugSettings{" +
				"enabled=" + enabled +
				", kpIndex=" + kpIndex +
				", degreesFromGeomagneticPole=" + degreesFromGeomagneticPole +
				", sunPosition=" + sunPosition +
				", cloudPercentage=" + cloudPercentage +
				'}';
	}
}
