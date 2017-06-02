package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import dagger.Module;
import dagger.Provides;
import java8.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
@Module
public abstract class ZoneIdModule {

	// Published
	@Provides
	static Supplier<ZoneId> provideZoneIdSupplier() {
		return ZoneOffset::systemDefault;
	}

}
