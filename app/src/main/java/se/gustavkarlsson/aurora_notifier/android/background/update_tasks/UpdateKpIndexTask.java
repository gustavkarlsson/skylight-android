package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.util.Log;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.providers.KpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class UpdateKpIndexTask extends RealmEnclosedAsyncTask<Object, Object, Object> {
	private static final String TAG = UpdateKpIndexTask.class.getSimpleName();

	private final KpIndexProvider provider;

	public UpdateKpIndexTask(KpIndexProvider provider) {
		this.provider = provider;
	}

	@Override
	protected Object doInBackgroundWithRealm(Realm realm, Object... params) {
		try {
			Log.i(TAG, "Getting KP index...");
			final Timestamped<Float> kpIndex = provider.getKpIndex();
			Log.d(TAG, "KP Index is: " + kpIndex);

			Log.d(TAG, "Looking up KP index from realm...");
			final RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
			Log.d(TAG, "Realm KP index is:  " + realmKpIndex);

			Log.d(TAG, "Storing KP index in realm");
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realmKpIndex.setKpIndex(kpIndex.getValue());
					realmKpIndex.setTimestamp(kpIndex.getTimestamp());
				}
			});
			Log.i(TAG, "Updated KP index in realm");
		} catch (ProviderException e) {
			e.printStackTrace();
		}
		return null;
	}
}
