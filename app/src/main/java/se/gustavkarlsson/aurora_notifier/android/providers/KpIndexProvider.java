package se.gustavkarlsson.aurora_notifier.android.providers;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public interface KpIndexProvider {
	Timestamped<Float> getKpIndex() throws ProviderException;
}
