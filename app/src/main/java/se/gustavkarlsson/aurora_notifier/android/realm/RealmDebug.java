package se.gustavkarlsson.aurora_notifier.android.realm;

import io.realm.RealmObject;

// TODO Don't add RealmDebug for release build
public class RealmDebug extends RealmObject {
	private boolean enabled;
	private Float kpIndex;
	private Float degreesFromClosestPole;
	private Float zenithAngle;
	private Integer cloudPercentage;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Float getKpIndex() {
		return kpIndex;
	}

	public void setKpIndex(Float kpIndex) {
		this.kpIndex = kpIndex;
	}

	public Float getDegreesFromClosestPole() {
		return degreesFromClosestPole;
	}

	public void setDegreesFromClosestPole(Float degreesFromClosestPole) {
		this.degreesFromClosestPole = degreesFromClosestPole;
	}

	public Float getZenithAngle() {
		return zenithAngle;
	}

	public void setZenithAngle(Float zenithAngle) {
		this.zenithAngle = zenithAngle;
	}

	public Integer getCloudPercentage() {
		return cloudPercentage;
	}

	public void setCloudPercentage(Integer cloudPercentage) {
		this.cloudPercentage = cloudPercentage;
	}

	@Override
	public String toString() {
		return "RealmDebug{" +
				"enabled=" + enabled +
				", kpIndex=" + kpIndex +
				", degreesFromClosestPole=" + degreesFromClosestPole +
				", zenithAngle=" + zenithAngle +
				", cloudPercentage=" + cloudPercentage +
				'}';
	}
}
