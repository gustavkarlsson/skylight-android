package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class ContextModule {
	private final Context context;

	public ContextModule(Context context) {
		this.context = context;
	}

	@Provides
	@Reusable
	Context provideContext() {
		return context;
	}
}
