package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.util.RelativeTimeFormatter;
import se.gustavkarlsson.aurora_notifier.android.util.TimeFormatter;

public class KpIndexViewModel extends RealmViewModel<RealmKpIndex> {
	private static final String NO_VALUE = "-";

	private final TimeFormatter timeFormatter = new RelativeTimeFormatter();

	public KpIndexViewModel(RealmKpIndex value) {
		super(value);
	}

	@Bindable
	public String getKpIndex() {
		Float kpIndex = getObject().getKpIndex();
		if (kpIndex == null) {
			return NO_VALUE;
		} else {
			CharSequence formattedTime = timeFormatter.formatTime(getObject().getTimestamp());
			return String.format(Locale.getDefault(), "Kp index: %.1f @ %s", kpIndex, formattedTime);
		}
	}
}
