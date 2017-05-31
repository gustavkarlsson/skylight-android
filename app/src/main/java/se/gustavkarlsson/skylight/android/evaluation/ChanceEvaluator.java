package se.gustavkarlsson.skylight.android.evaluation;

public interface ChanceEvaluator<T> {
	Chance evaluate(T value);
}
