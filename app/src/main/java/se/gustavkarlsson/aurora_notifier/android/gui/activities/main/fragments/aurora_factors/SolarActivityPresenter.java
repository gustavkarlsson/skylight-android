package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;

class SolarActivityPresenter extends AbstractAuroraFactorPresenter {
	SolarActivityPresenter(AuroraFactorView factorView) {
		super(factorView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_solar_activity_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_solar_activity_desc;
	}

	void onUpdate(SolarActivity solarActivity) {
		float kpIndex = solarActivity.getKpIndex();
		int whole = (int) kpIndex;
		float part = kpIndex - whole;
		String partString = parsePart(part);
		String wholeString = parseWhole(whole, partString);
		setFactorValue(wholeString + partString);
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

	private static String parseWhole(int whole, String partString) {
		int wholeAdjusted = partString.equals("-") ? whole + 1 : whole;
		return Integer.toString(wholeAdjusted);
	}
}
