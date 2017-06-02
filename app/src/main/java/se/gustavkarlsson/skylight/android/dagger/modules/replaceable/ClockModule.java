package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import org.threeten.bp.Clock;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public abstract class ClockModule {

	// Published
	@Provides
	@Reusable
	static Clock provideClock() {
		return Clock.systemUTC();
	}

}
