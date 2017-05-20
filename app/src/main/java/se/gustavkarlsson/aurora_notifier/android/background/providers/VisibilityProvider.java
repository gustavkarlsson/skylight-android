package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

public interface VisibilityProvider {
	Visibility getVisibility(double latitude, double longitude);
}
