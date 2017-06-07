package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import org.threeten.bp.Duration;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

import static se.gustavkarlsson.skylight.android.dagger.Names.BACKGROUND_UPDATE_TIMEOUT_NAME;

@Module
public abstract class BackgroundUpdateTimeoutModule {

	// Published
	@Provides
	@Reusable
	@Named(BACKGROUND_UPDATE_TIMEOUT_NAME)
	static Duration provideBackgroundUpdateTimeout() {
		return Duration.ofSeconds(30);
	}

}
