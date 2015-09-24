package se.gustavkarlsson.aurora_notifier.android.domain.place;

import se.gustavkarlsson.aurora_notifier.android.AuroraNotifier;
import se.gustavkarlsson.aurora_notifier.android.R;

public final class CurrentPlace implements Place {

	@Override
	public String getName() {
		return AuroraNotifier.getContext().getResources().getString(R.string.current_place);
	}

	@Override
	public Position getPosition() {
		// TODO get latest position from location API
		return null;
	}

	@Override
	public String toString() {
		return "CurrentPlace{" +
				"name='" + getName() + '\'' +
				", position=" + getPosition() +
				'}';
	}
}
