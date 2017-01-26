package se.gustavkarlsson.aurora_notifier.android.evaluation;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

public interface ComplicationEvaluator {
	List<AuroraComplication> evaluate();
}
