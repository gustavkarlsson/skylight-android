package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;
import se.gustavkarlsson.aurora_notifier.android.util.DumbTimeFormatter;
import se.gustavkarlsson.aurora_notifier.android.util.TimeFormatter;

public class WeatherViewModel extends RealmViewModel<RealmWeather> {
	private static final String NO_VALUE = "-";

	private final TimeFormatter timeFormatter = new DumbTimeFormatter();

	public WeatherViewModel(RealmWeather value) {
		super(value);
	}

	@Bindable
	public String getCloudPercentage() {
		Integer cloudPercentage = getObject().getCloudPercentage();
		if (cloudPercentage == null) {
			return NO_VALUE;
		} else {
			String formattedTime = timeFormatter.formatTime(getObject().getTimestamp());
			return String.format(Locale.getDefault(), "Clouds: %d%%\n%s", cloudPercentage, formattedTime);
		}
	}
}
