package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public interface AuroraChanceEvaluator {
	AuroraChance evaluate(AuroraReport auroraReport);
}
