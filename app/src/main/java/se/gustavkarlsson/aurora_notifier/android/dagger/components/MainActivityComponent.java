package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.PersistentCacheModule;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.MainActivity;

@Component(modules = PersistentCacheModule.class)
@SuppressWarnings("WeakerAccess")
public interface MainActivityComponent {
	void inject(MainActivity mainActivity);
}
