package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.GeomagActivityEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

class GeomagActivityPresenter extends AbstractAuroraFactorPresenter<GeomagActivity> {

	GeomagActivityPresenter(AuroraFactorView factorView) {
		super(factorView, new GeomagActivityEvaluator());
	}

	@Override
	int getFullTitleResourceId() {
		return R.string.factor_geomag_activity_title_full;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_geomag_activity_desc;
	}

	@Override
	String evaluateText(GeomagActivity geomagActivity) {
		Float kpIndex = geomagActivity.getKpIndex();
		if (kpIndex == null) {
			return "?";
		}
		int whole = kpIndex.intValue();
		float part = kpIndex - whole;
		String partString = parsePart(part);
		String wholeString = parseWhole(whole, partString);
		return wholeString + partString;
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
