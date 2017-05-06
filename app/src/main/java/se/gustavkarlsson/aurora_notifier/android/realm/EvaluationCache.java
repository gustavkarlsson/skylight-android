package se.gustavkarlsson.aurora_notifier.android.realm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.parceler.Parcels;

import io.realm.Realm;
import io.realm.RealmObject;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation$$Parcelable;

public class EvaluationCache extends RealmObject {
	private static final String TAG = EvaluationCache.class.getSimpleName();

	private byte[] data;

	public static AuroraEvaluation get() {
		try (Realm realm = Realm.getDefaultInstance()) {
			EvaluationCache cache = getSingleton(realm);
			if (cache.data == null) {
				return null;
			}
			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(cache.data, 0, cache.data.length);
			parcel.setDataPosition(0);
			AuroraEvaluation evaluation = AuroraEvaluation$$Parcelable.CREATOR.createFromParcel(parcel).getParcel();
			parcel.recycle();
			return evaluation;
		} catch (RuntimeException e) {
			Log.e(TAG, "Error when getting evaluation from cache. Clearing cache.");
			clear();
			return null;
		}
	}

	public static void set(AuroraEvaluation evaluation) {
		if (evaluation == null) {
			clear();
			return;
		}
		try (Realm realm = Realm.getDefaultInstance()) {
			Parcelable parcelable = Parcels.wrap(evaluation);
			Parcel parcel = Parcel.obtain();
			parcelable.writeToParcel(parcel, 0);
			byte[] bytes = parcel.marshall();
			EvaluationCache cache = getSingleton(realm);
			realm.executeTransaction(r -> cache.data = bytes);
		}
	}

	private static void clear() {
		try (Realm realm = Realm.getDefaultInstance()) {
			EvaluationCache cache = getSingleton(realm);
			realm.executeTransaction(r -> cache.data = null);
		}
	}

	private static EvaluationCache getSingleton(Realm realm) {
		return realm.where(EvaluationCache.class).findFirst();
	}
}
