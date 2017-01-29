package se.gustavkarlsson.aurora_notifier.android.evaluation;


import java.util.Arrays;
import java.util.List;

import java8.util.stream.Collectors;
import se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators.SolarActivityAndGeomagneticLocationComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators.SunPositionComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.factor_evaluators.WeatherComplicationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;

import static java8.util.stream.StreamSupport.stream;


public class AuroraDataComplicationsEvaluator implements ComplicationEvaluator {
	private final List<ComplicationEvaluator> complicationEvaluators;

	public AuroraDataComplicationsEvaluator(AuroraData data) {
		complicationEvaluators = Arrays.asList(
				new WeatherComplicationEvaluator(data.getWeather()),
				new SunPositionComplicationEvaluator(data.getSunPosition()),
				new SolarActivityAndGeomagneticLocationComplicationEvaluator(data.getSolarActivity(), data.getGeomagneticLocation())
		);
	}

	@Override
	public List<AuroraComplication> evaluate() {
		return stream(complicationEvaluators)
				.flatMap(evaluator -> stream(evaluator.evaluate()))
				.collect(Collectors.toList());
	}
}
