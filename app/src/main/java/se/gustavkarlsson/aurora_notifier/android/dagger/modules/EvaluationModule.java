package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.DarknessEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.GeomagActivityEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.GeomagLocationEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.VisibilityEvaluator;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

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

}
