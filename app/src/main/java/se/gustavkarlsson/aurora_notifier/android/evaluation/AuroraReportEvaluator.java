package se.gustavkarlsson.aurora_notifier.android.evaluation;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import java8.util.stream.RefStreams;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

import static java8.util.stream.StreamSupport.stream;

public class AuroraReportEvaluator implements ChanceEvaluator<AuroraReport> {

	private final GeomagActivityEvaluator geomagActivityEvaluator;
	private final GeomagLocationEvaluator geomagLocationEvaluator;
	private final VisibilityEvaluator visibilityEvaluator;
	private final DarknessEvaluator darknessEvaluator;

	@Inject
	public AuroraReportEvaluator(GeomagActivityEvaluator geomagActivityEvaluator, GeomagLocationEvaluator geomagLocationEvaluator, VisibilityEvaluator visibilityEvaluator, DarknessEvaluator darknessEvaluator) {
		this.geomagActivityEvaluator = geomagActivityEvaluator;
		this.geomagLocationEvaluator = geomagLocationEvaluator;
		this.visibilityEvaluator = visibilityEvaluator;
		this.darknessEvaluator = darknessEvaluator;
	}

	@Override
	public Chance evaluate(AuroraReport auroraReport) {
		AuroraFactors factors = auroraReport.getFactors();
		Chance activityChance = geomagActivityEvaluator.evaluate(factors.getGeomagActivity());
		Chance locationChance = geomagLocationEvaluator.evaluate(factors.getGeomagLocation());
		Chance visibilityChance = visibilityEvaluator.evaluate(factors.getVisibility());
		Chance darknessChance = darknessEvaluator.evaluate(factors.getDarkness());

		List<Chance> chances = Arrays.asList(activityChance, locationChance, visibilityChance, darknessChance);

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
