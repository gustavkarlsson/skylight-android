package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.util.DumbTimeFormatter;
import se.gustavkarlsson.aurora_notifier.android.util.TimeFormatter;

public class KpIndexViewModel extends RealmViewModel<RealmKpIndex> {
	private static final String NO_VALUE = "-";

	private final TimeFormatter timeFormatter = new DumbTimeFormatter();

	public KpIndexViewModel(RealmKpIndex value) {
		super(value);
	}

	@Bindable
	public String getKpIndex() {
		Float kpIndex = getObject().getKpIndex();
		if (kpIndex == null) {
			return NO_VALUE;
		} else {
			String formattedTime = timeFormatter.formatTime(getObject().getTimestamp());
			return String.format(Locale.getDefault(), "Kp index: %.1f\n%s", kpIndex, formattedTime);
		}
	}
}
