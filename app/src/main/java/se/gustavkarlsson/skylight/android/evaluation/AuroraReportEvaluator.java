package se.gustavkarlsson.skylight.android.evaluation;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import java8.util.stream.RefStreams;
import se.gustavkarlsson.skylight.android.models.AuroraFactors;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

import static java8.util.stream.StreamSupport.stream;

public class AuroraReportEvaluator implements ChanceEvaluator<AuroraReport> {

	private final ChanceEvaluator<GeomagActivity> geomagActivityEvaluator;
	private final ChanceEvaluator<GeomagLocation> geomagLocationEvaluator;
	private final ChanceEvaluator<Visibility> visibilityEvaluator;
	private final ChanceEvaluator<Darkness> darknessEvaluator;

	@Inject
	AuroraReportEvaluator(ChanceEvaluator<GeomagActivity> geomagActivityEvaluator, ChanceEvaluator<GeomagLocation> geomagLocationEvaluator, ChanceEvaluator<Visibility> visibilityEvaluator, ChanceEvaluator<Darkness> darknessEvaluator) {
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
