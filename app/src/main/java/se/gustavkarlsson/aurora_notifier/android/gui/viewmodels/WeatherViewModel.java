package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

public class WeatherViewModel extends RealmViewModel<RealmWeather> {
	public WeatherViewModel(RealmWeather value) {
		super(value);
	}

	@Bindable
	public String getCloudPercentage() {
		return getValue().getCloudPercentage() == null ? "-" : "" + getValue().getCloudPercentage() + "% @ " + getValue().getTimestamp();
	}
}
