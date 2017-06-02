package se.gustavkarlsson.skylight.android.settings;

import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;

public interface Settings {
	boolean isEnableNotifications();

	ChanceLevel getTriggerLevel();
}
