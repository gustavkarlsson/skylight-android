package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import org.threeten.bp.Duration;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

import static se.gustavkarlsson.skylight.android.dagger.Names.FOREGROUND_UPDATE_TIMEOUT_NAME;

@Module
public abstract class ForegroundUpdateTimeoutModule {

	// Published
	@Provides
	@Reusable
	@Named(FOREGROUND_UPDATE_TIMEOUT_NAME)
	static Duration provideForegroundUpdateTimeout() {
		return Duration.ofSeconds(10);
	}

}
