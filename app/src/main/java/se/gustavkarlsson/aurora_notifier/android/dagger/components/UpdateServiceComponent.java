package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.UpdateServiceModule;

@Singleton
@Component(modules = UpdateServiceModule.class)
public interface UpdateServiceComponent { // Must be public
	void inject(UpdateService updateService);
}
