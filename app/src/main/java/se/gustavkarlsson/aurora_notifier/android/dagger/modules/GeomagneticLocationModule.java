package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import se.gustavkarlsson.aurora_notifier.android.background.providers.GeomagneticLocationProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.GeomagneticLocationProviderImpl;

@Module
public abstract class GeomagneticLocationModule {

	@Binds
	@Reusable
	abstract GeomagneticLocationProvider bindGeomagneticLocationProvider(GeomagneticLocationProviderImpl provider);

}
