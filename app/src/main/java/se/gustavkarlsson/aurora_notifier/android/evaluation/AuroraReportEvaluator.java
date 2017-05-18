package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

import static se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance.UNKNOWN;

public class AuroraReportEvaluator implements AuroraChanceEvaluator {
	@Override
	public AuroraChance evaluate(AuroraReport auroraReport) {
		return UNKNOWN; // FIXME implement
	}
}
