package se.gustavkarlsson.aurora_notifier.android.gui;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public interface AuroraEvaluationUpdateReceiver {
	void update(AuroraEvaluation evaluation);
}
