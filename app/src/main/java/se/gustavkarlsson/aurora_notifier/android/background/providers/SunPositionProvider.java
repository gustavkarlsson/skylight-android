package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public interface SunPositionProvider {
	Timestamped<Float> getZenithAngle(long timeMillis, double latitude, double longitude) throws ProviderException;
}
