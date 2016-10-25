package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.location.Location;
import android.util.Log;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticCoordinatesProvider;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class UpdateGeomagneticCoordinatesTask extends RealmEnclosedAsyncTask<Object, Object, Object> {
	private static final String TAG = UpdateGeomagneticCoordinatesTask.class.getSimpleName();

	private final GeomagneticCoordinatesProvider provider;
	private final Location location;

	public UpdateGeomagneticCoordinatesTask(GeomagneticCoordinatesProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	protected Object doInBackgroundWithRealm(Realm realm, Object... params) {
		Log.i(TAG, "Getting degrees from closest pole...");
		final Timestamped<Float> degreesFromClosestPole = provider.getDegreesFromClosestPole(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Degrees from closest pole is: " + degreesFromClosestPole);

		Log.d(TAG, "Looking up geomagnetic coordinates from realm...");
		final RealmGeomagneticCoordinates realmGeomagneticCoordinates = realm.where(RealmGeomagneticCoordinates.class).findFirst();
		Log.d(TAG, "Realm geomagnetic coordinates are:  " + realmGeomagneticCoordinates);

		Log.d(TAG, "Storing degrees from closest pole in realm");
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				realmGeomagneticCoordinates.setDegreesFromClosestPole(degreesFromClosestPole.getValue());
				realmGeomagneticCoordinates.setTimestamp(degreesFromClosestPole.getTimestamp());
			}
		});
		Log.i(TAG, "Updated geomagnetic coordinates in realm");
		return null;
	}
}
