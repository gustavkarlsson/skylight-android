package se.gustavkarlsson.aurora_notifier.android.gui.viewmodels;

import android.databinding.Bindable;

import java.util.Locale;

import se.gustavkarlsson.aurora_notifier.android.realm.RealmGeomagneticCoordinates;
import se.gustavkarlsson.aurora_notifier.android.util.DumbTimeFormatter;
import se.gustavkarlsson.aurora_notifier.android.util.TimeFormatter;

public class GeomagneticCoordinatesViewModel extends RealmViewModel<RealmGeomagneticCoordinates> {
	private static final String NO_VALUE = "-";

	private final TimeFormatter timeFormatter = new DumbTimeFormatter();

	public GeomagneticCoordinatesViewModel(RealmGeomagneticCoordinates value) {
		super(value);
	}

	@Bindable
	public String getDegreesFromClosestPole() {
		Float degreesFromClosestPole = getObject().getDegreesFromClosestPole();
		if (degreesFromClosestPole == null) {
			return NO_VALUE;
		} else {
			String formattedTime = timeFormatter.formatTime(getObject().getTimestamp());
			return String.format(Locale.getDefault(), "Degrees: %.1f°\n%s", degreesFromClosestPole, formattedTime);
		}
	}
}
