package se.gustavkarlsson.skylight.android.settings;

public interface DebugSettings {
	boolean isOverrideValues();

	float getKpIndex();

	float getGeomagLatitude();

	float getSunZenithAngle();

	int getCloudPercentage();
}
