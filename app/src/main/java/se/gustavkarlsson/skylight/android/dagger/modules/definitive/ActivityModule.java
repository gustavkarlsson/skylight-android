package se.gustavkarlsson.skylight.android.dagger.modules.definitive;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope;

@Module
public class ActivityModule {
	private final Activity activity;

	public ActivityModule(Activity activity) {
		this.activity = activity;
	}

	@Provides
	@ActivityScope
	Activity provideActivity() {
		return activity;
	}
}
