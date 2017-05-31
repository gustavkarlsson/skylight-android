package se.gustavkarlsson.skylight.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.skylight.android.dagger.modules.EvaluationModule;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

@Component(modules = EvaluationModule.class)
@SuppressWarnings("WeakerAccess")
public interface EvaluationComponent {
	void inject(AuroraChanceFragment auroraChanceFragment);
	ChanceEvaluator<GeomagActivity> getGeomagActivityEvaluator();
	ChanceEvaluator<GeomagLocation> getGeomagLocationEvaluator();
	ChanceEvaluator<Visibility> getVisibilityEvaluator();
	ChanceEvaluator<Darkness> getDarknessEvaluator();
}
