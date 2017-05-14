package se.gustavkarlsson.aurora_notifier.android.evaluation;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public interface AuroraChanceEvaluator {
	AuroraChance evaluate(AuroraEvaluation auroraEvaluation);
}
