package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.observers.ObservableData;

@Module
public abstract class LatestAuroraReportObservableModule {

	@Binds
	@Singleton
	@Named("Latest")
	abstract ObservableData<AuroraReport> bindLatestAuroraReportObservable(ObservableData<AuroraReport> impl);
}
