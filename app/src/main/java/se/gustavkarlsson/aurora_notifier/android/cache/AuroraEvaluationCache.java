package se.gustavkarlsson.aurora_notifier.android.cache;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public interface AuroraEvaluationCache {

	AuroraEvaluation getCurrentLocation();

	void setCurrentLocation(AuroraEvaluation evaluation);
}
