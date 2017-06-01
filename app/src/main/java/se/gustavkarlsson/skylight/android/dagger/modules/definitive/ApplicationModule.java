package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class ApplicationModule {
	private final Application application;

	public ApplicationModule(Application application) {
		this.application = application;
	}

	@Provides
	@Reusable
	Application provideApplication() {
		return application;
	}

	@Provides
	@Reusable
	Context provideContext() {
		return application;
	}
}
