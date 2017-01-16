package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;
import se.gustavkarlsson.aurora_notifier.android.gui.time.RelativeTimeFormatter;
import se.gustavkarlsson.aurora_notifier.android.gui.time.TimeFormatter;

public class WeatherViewModel extends RealmViewModel<RealmWeather> {
	private static final String NO_VALUE = "-";

	private final TimeFormatter timeFormatter = new RelativeTimeFormatter();

	public WeatherViewModel(RealmWeather value) {
		super(value);
	}

	@Bindable
	public String getCloudPercentage() {
		Integer cloudPercentage = getObject().getCloudPercentage();
		if (cloudPercentage == null) {
			return NO_VALUE;
		} else {
			CharSequence formattedTime = timeFormatter.formatTime(getObject().getTimestamp());
			return String.format(Locale.getDefault(), "Clouds: %d%% @ %s", cloudPercentage, formattedTime);
		}
	}
}
