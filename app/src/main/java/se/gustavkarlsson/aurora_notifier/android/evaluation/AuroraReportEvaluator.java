package se.gustavkarlsson.aurora_notifier.android.evaluation;

import java.util.Arrays;
import java.util.List;

import java8.util.stream.RefStreams;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

import static java8.util.stream.StreamSupport.stream;

public class AuroraReportEvaluator implements ChanceEvaluator<AuroraReport> {

	@Override
	public Chance evaluate(AuroraReport auroraReport) {
		AuroraFactors factors = auroraReport.getFactors();
		Chance weatherChance = new WeatherEvaluator().evaluate(factors.getWeather());
		Chance sunPositionChance = new SunPositionEvaluator().evaluate(factors.getSunPosition());
		Chance geomagChance = new GeomagActivityEvaluator().evaluate(factors.getGeomagActivity());
		Chance locationChance = new GeomagLocationEvaluator().evaluate(factors.getGeomagLocation());

		List<Chance> chances = Arrays.asList(weatherChance, sunPositionChance, geomagChance, locationChance);

		if (stream(chances).anyMatch(c -> !c.isKnown())) {
			return Chance.unknown();
		}

		if (stream(chances).anyMatch(c -> !c.isPossible())) {
			return Chance.impossible();
		}

		Chance geomagAndLocationChance = Chance.of(geomagChance.getValue() * locationChance.getValue());

		return RefStreams.of(weatherChance, sunPositionChance, geomagAndLocationChance)
				.min(Chance::compareTo)
				.orElseThrow(() -> new IllegalStateException("No PresentableChance supplied"));
	}
}
