package se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators;

import java.util.LinkedList;
import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;

import static se.gustavkarlsson.aurora_notifier.android.models.AuroraChance.LOW;
import static se.gustavkarlsson.aurora_notifier.android.models.AuroraChance.NONE;

public class SunPositionComplicationEvaluator implements ComplicationEvaluator {
	private final SunPosition sunPosition;

	public SunPositionComplicationEvaluator(SunPosition sunPosition) {
		this.sunPosition = sunPosition;
	}

	@Override
	public List<AuroraComplication> evaluate() {
		List<AuroraComplication> complications = new LinkedList<>();
		float zenithAngle = sunPosition.getZenithAngle();

		if (zenithAngle < 90) {
			complications.add(new AuroraComplication(
					NONE,
					R.string.complication_daylight_title,
					R.string.complication_daylight_desc));
		} else if (zenithAngle < 95) {
			complications.add(new AuroraComplication(
					LOW,
					R.string.complication_twilight_title,
					R.string.complication_twilight_desc));
		}
		return complications;
	}
}
