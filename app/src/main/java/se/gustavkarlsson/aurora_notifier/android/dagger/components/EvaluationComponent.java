package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.EvaluationModule;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;

@Component(modules = EvaluationModule.class)
@SuppressWarnings("WeakerAccess")
public interface EvaluationComponent {
	void inject(AuroraChanceFragment auroraChanceFragment);
	ChanceEvaluator<GeomagActivity> getGeomagActivityEvaluator();
	ChanceEvaluator<GeomagLocation> getGeomagLocationEvaluator();
	ChanceEvaluator<Visibility> getVisibilityEvaluator();
	ChanceEvaluator<Darkness> getDarknessEvaluator();
}
