package se.gustavkarlsson.aurora_notifier.android.evaluation;

public interface ChanceEvaluator<T> {
	Chance evaluate(T value);
}
