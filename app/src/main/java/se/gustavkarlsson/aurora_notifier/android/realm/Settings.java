package se.gustavkarlsson.aurora_notifier.android.realm;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * WARNING: Any change to this file between releases will require a migration!
 */
public class Settings extends RealmObject {
	private boolean notifications;

	public static Settings get(Realm realm) {
		return realm.where(Settings.class).findFirst();
	}

	public boolean isNotifications() {
		return notifications;
	}

	public void setNotifications(boolean notifications) {
		this.notifications = notifications;
	}

	@Override
	public String toString() {
		return "Settings{" +
				"notifications=" + notifications +
				'}';
	}
}
