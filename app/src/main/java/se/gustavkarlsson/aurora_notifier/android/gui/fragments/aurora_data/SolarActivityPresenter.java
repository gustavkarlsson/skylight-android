package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;

class SolarActivityPresenter extends AbstractAuroraDataPresenter {
	SolarActivityPresenter(AuroraDataView dataView) {
		super(dataView);
	}

	void update(SolarActivity solarActivity) {
		float kpIndex = solarActivity.getKpIndex();
		int whole = (int) kpIndex;
		float part = kpIndex - whole;
		String partString = parsePart(part);
		String wholeString = partString.equals("-") ? Integer.toString(whole + 1) : Integer.toString(whole);
		setDataValue(wholeString + partString);
	}

	private static String parsePart(float part) {
		if (part < 0.15) {
			return "";
		}
		if (part < 0.5) {
			return "+";
		}
		return "-";
	}
}
