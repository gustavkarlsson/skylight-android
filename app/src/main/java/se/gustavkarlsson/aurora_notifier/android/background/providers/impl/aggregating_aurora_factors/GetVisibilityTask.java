package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.VisibilityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

class GetVisibilityTask extends FutureTask<Visibility> {
	private static final String TAG = GetVisibilityTask.class.getSimpleName();

	GetVisibilityTask(VisibilityProvider provider, Location location) {
		super(() -> call(provider, location));
	}

	private static Visibility call(VisibilityProvider provider, Location location) throws Exception {
		Log.i(TAG, "Getting visibility...");
		Visibility visibility = provider.getVisibility(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Visibility is:  " + visibility);
		return visibility;
	}
}
