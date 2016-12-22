package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmSunPosition;
import se.gustavkarlsson.aurora_notifier.android.util.DumbTimeFormatter;
import se.gustavkarlsson.aurora_notifier.android.util.TimeFormatter;

public class SunPositionViewModel extends RealmViewModel<RealmSunPosition> {
	private static final String NO_VALUE = "-";

	private final TimeFormatter timeFormatter = new DumbTimeFormatter();

	public SunPositionViewModel(RealmSunPosition value) {
		super(value);
	}

	@Bindable
	public String getZenithAngle() {
		Float zenithAngle = getObject().getZenithAngle();
		if (zenithAngle == null) {
			return NO_VALUE;
		} else {
			String formattedTime = timeFormatter.formatTime(getObject().getTimestamp());
			return String.format(Locale.getDefault(), "Zenith angle: %.1f°\n%s", zenithAngle, formattedTime);
		}
	}
}
