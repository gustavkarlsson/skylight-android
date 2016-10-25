package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.location.Location;
import android.util.Log;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class UpdateSunPositionTask extends RealmEnclosedAsyncTask<Object, Object, Object> {
	private static final String TAG = UpdateSunPositionTask.class.getSimpleName();

	private final SunPositionProvider provider;
	private final Location location;
	private final long timeMillis;

	public UpdateSunPositionTask(SunPositionProvider provider, Location location, long timeMillis) {
		this.provider = provider;
		this.location = location;
		this.timeMillis = timeMillis;
	}

	@Override
	protected Object doInBackgroundWithRealm(Realm realm, Object... params) {
		try {
			Log.i(TAG, "Getting sun position...");
			final Timestamped<Float> zenithAngle = provider.getZenithAngle(timeMillis, location.getLatitude(), location.getLongitude());
			Log.d(TAG, "Sun position is: " + zenithAngle);

			Log.d(TAG, "Looking up sun position from realm...");
			final RealmSunPosition realmSunPosition = realm.where(RealmSunPosition.class).findFirst();
			Log.d(TAG, "Realm sun position is:  " + realmSunPosition);

			Log.d(TAG, "Storing sun position in realm");
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realmSunPosition.setZenithAngle(zenithAngle.getValue());
					realmSunPosition.setTimestamp(zenithAngle.getTimestamp());
				}
			});
			Log.i(TAG, "Updated sun position in realm");
		} catch (ProviderException e) {
			e.printStackTrace();
		}
		return null;
	}
}
