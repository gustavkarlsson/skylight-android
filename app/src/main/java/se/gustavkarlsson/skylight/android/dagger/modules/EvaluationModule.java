package se.gustavkarlsson.skylight.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.skylight.android.evaluation.AuroraReportEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.DarknessEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.GeomagActivityEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.GeomagLocationEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.VisibilityEvaluator;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

@Module
public abstract class EvaluationModule {

	@Binds
	@Reusable
	abstract ChanceEvaluator<GeomagActivity> bindGeomagActivityEvaluator(GeomagActivityEvaluator evaluator);

	@Binds
	@Reusable
	abstract ChanceEvaluator<GeomagLocation> bindGeomagLocationEvaluator(GeomagLocationEvaluator evaluator);

	@Binds
	@Reusable
	abstract ChanceEvaluator<Visibility> bindVisibilityEvaluator(VisibilityEvaluator evaluator);

	@Binds
	@Reusable
	abstract ChanceEvaluator<Darkness> bindDarknessEvaluator(DarknessEvaluator evaluator);

	@Binds
	@Reusable
	abstract ChanceEvaluator<AuroraReport> bindAuroraReportEvaluator(AuroraReportEvaluator evaluator);

}
