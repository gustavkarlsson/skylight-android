package se.gustavkarlsson.aurora_notifier.android.util;

import android.text.format.DateUtils;

public final class RelativeTimeFormatter implements TimeFormatter {
	public CharSequence formatTime(Long millis) {
		return DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
	}
}
