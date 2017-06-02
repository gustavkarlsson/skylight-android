package se.gustavkarlsson.skylight.android.dagger.components;

import org.threeten.bp.Clock;

import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.skylight.android.background.UpdateJob;
import se.gustavkarlsson.skylight.android.background.UpdateScheduler;
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.EvaluationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.AuroraReportCacheModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.AuroraReportModule;
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.ClockModule;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

@Component(modules = {
		AuroraReportCacheModule.class,
		EvaluationModule.class,
		AuroraReportModule.class,
		ClockModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	AuroraReportProvider getAuroraReportProvider();
	UpdateJob getUpdateJob();
	UpdateScheduler getUpdateScheduler();
	Clock getClock();

	ChanceEvaluator<GeomagActivity> getGeomagActivityEvaluator();
	ChanceEvaluator<GeomagLocation> getGeomagLocationEvaluator();
	ChanceEvaluator<Visibility> getVisibilityEvaluator();
	ChanceEvaluator<Darkness> getDarknessEvaluator();

	void inject(AuroraChanceFragment auroraChanceFragment);

	MainActivityComponent getMainActivityComponent(ActivityModule activityModule);
}
