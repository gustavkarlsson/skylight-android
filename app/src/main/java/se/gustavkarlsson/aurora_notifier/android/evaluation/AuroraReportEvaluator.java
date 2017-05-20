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
		Chance visibilityChance = new VisibilityEvaluator().evaluate(factors.getVisibility());
		Chance darknessChance = new DarknessEvaluator().evaluate(factors.getDarkness());
		Chance activityChance = new GeomagActivityEvaluator().evaluate(factors.getGeomagActivity());
		Chance locationChance = new GeomagLocationEvaluator().evaluate(factors.getGeomagLocation());

		List<Chance> chances = Arrays.asList(visibilityChance, darknessChance, activityChance, locationChance);

		if (stream(chances).anyMatch(c -> !c.isKnown())) {
			return Chance.unknown();
		}

		if (stream(chances).anyMatch(c -> !c.isPossible())) {
			return Chance.impossible();
		}

		Chance geomagActivityAndLocationChance = Chance.of(activityChance.getValue() * locationChance.getValue());

		return RefStreams.of(visibilityChance, darknessChance, geomagActivityAndLocationChance)
				.min(Chance::compareTo)
				.orElseThrow(() -> new IllegalStateException("No PresentableChance supplied"));
	}
}
