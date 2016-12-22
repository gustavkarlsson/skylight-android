package se.gustavkarlsson.aurora_notifier.android.util;

import java.util.Date;

public final class DumbTimeFormatter implements TimeFormatter {
	public String formatTime(Long millis) {
		return new Date(millis).toString();
	}
}
