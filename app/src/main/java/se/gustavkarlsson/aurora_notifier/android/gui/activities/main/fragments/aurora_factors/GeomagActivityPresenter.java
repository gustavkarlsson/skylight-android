package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.GeomagActivityEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;

class GeomagActivityPresenter extends AbstractAuroraFactorPresenter {
	private final GeomagActivityEvaluator evaluator = new GeomagActivityEvaluator();

	GeomagActivityPresenter(AuroraFactorView factorView) {
		super(factorView);
	}

	@Override
	int getTitleResourceId() {
		return R.string.factor_geomag_activity_title;
	}

	@Override
	int getDescriptionResourceId() {
		return R.string.factor_geomag_activity_desc;
	}

	void onUpdate(GeomagActivity geomagActivity) {
		setBackgroundColor(evaluator.evaluate(geomagActivity));
		setFactorValue(evaluateText(geomagActivity));
	}

	private static String evaluateText(GeomagActivity geomagActivity) {
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
