package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public interface AuroraEvaluationProvider {
	AuroraEvaluation getEvaluation(long timeoutMillis);
}
