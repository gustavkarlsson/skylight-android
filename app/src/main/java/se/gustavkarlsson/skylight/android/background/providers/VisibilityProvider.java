package se.gustavkarlsson.skylight.android.background.providers;

import se.gustavkarlsson.skylight.android.models.factors.Visibility;

public interface VisibilityProvider {
	Visibility getVisibility(double latitude, double longitude);
}
