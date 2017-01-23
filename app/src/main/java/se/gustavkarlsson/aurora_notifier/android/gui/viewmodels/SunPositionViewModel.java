package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.gui.time.RelativeTimeFormatter;
import se.gustavkarlsson.aurora_notifier.android.gui.time.TimeFormatter;

public class SunPositionViewModel extends RealmViewModel<RealmSunPosition> {
	private static final String NO_VALUE = "-";

	private final TimeFormatter timeFormatter = new RelativeTimeFormatter();

	public SunPositionViewModel(RealmSunPosition value) {
		super(value);
	}

	@Bindable
	public String getZenithAngle() {
		Float zenithAngle = getObject().getZenithAngle();
		if (zenithAngle == null) {
			return NO_VALUE;
		} else {
			CharSequence formattedTime = timeFormatter.formatTime(getObject().getTimestamp());
			return String.format(Locale.getDefault(), "%.1f° @ %s", zenithAngle, formattedTime);
		}
	}
}
