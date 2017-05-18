package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GeomagLocationProviderImpl;

@Module
public abstract class GeomagLocationModule {

	@Binds
	@Reusable
	abstract GeomagLocationProvider bindGeomagLocationProvider(GeomagLocationProviderImpl provider);

}
