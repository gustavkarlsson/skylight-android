package se.gustavkarlsson.aurora_notifier.android.realm.place;

import io.realm.RealmObject;
import io.realm.annotations.Required;
import se.gustavkarlsson.aurora_notifier.android.domain.place.Place;

public class RealmPlace extends RealmObject implements Place {

	@Required
	private String name;

	private RealmPosition position;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public RealmPosition getPosition() {
		return position;
	}

	public void setPosition(RealmPosition position) {
		this.position = position;
	}
}
