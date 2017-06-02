package se.gustavkarlsson.skylight.android.dagger.components;

import android.content.Context;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.skylight.android.cache.AuroraReportCache;
import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ContextModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.EvaluationModule;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ExecutorModule;
import se.gustavkarlsson.skylight.android.dagger.modules.prod.AuroraReportCacheModule;
import se.gustavkarlsson.skylight.android.dagger.modules.prod.ReportNotificationCacheModule;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.models.factors.Darkness;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;
import se.gustavkarlsson.skylight.android.models.factors.Visibility;

import static se.gustavkarlsson.skylight.android.dagger.modules.definitive.ExecutorModule.CACHED_THREAD_POOL_NAME;

@Component(modules = {
		ContextModule.class,
		ReportNotificationCacheModule.class,
		AuroraReportCacheModule.class,
		ExecutorModule.class,
		EvaluationModule.class
})
@Singleton
@SuppressWarnings("WeakerAccess")
public interface ApplicationComponent {
	Context getContext();
	AuroraReportCache getAuroraReportCache();
	ReportNotificationCache getReportNotificationCache();
	@Named(CACHED_THREAD_POOL_NAME)
	ExecutorService getCachedThreadPool();

	// Evaluation stuff
	void inject(AuroraChanceFragment auroraChanceFragment);
	ChanceEvaluator<AuroraReport> getAuroraReportEvaluator();
	ChanceEvaluator<GeomagActivity> getGeomagActivityEvaluator();
	ChanceEvaluator<GeomagLocation> getGeomagLocationEvaluator();
	ChanceEvaluator<Visibility> getVisibilityEvaluator();
	ChanceEvaluator<Darkness> getDarknessEvaluator();
}
