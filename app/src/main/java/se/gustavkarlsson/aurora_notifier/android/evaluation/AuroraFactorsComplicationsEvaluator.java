package se.gustavkarlsson.aurora_notifier.android.evaluation;

import java.util.Arrays;
import java.util.List;

import java8.util.stream.Collectors;
import se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators.SolarActivityAndGeomagneticLocationComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators.SunPositionComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators.WeatherComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;

import static java8.util.stream.StreamSupport.stream;

public class AuroraFactorsComplicationsEvaluator implements ComplicationEvaluator {
	private final List<ComplicationEvaluator> complicationEvaluators;

	public AuroraFactorsComplicationsEvaluator(AuroraFactors factors) {
		complicationEvaluators = Arrays.asList(
				new WeatherComplicationEvaluator(factors.getWeather()),
				new SunPositionComplicationEvaluator(factors.getSunPosition()),
				new SolarActivityAndGeomagneticLocationComplicationEvaluator(factors.getSolarActivity(), factors.getGeomagneticLocation())
		);
	}

	@Override
	public List<AuroraComplication> evaluate() {
		return stream(complicationEvaluators)
				.flatMap(evaluator -> stream(evaluator.evaluate()))
				.collect(Collectors.toList());
	}
}
