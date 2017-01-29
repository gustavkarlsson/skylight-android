package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.CacheModule;
import se.gustavkarlsson.aurora_notifier.android.gui.fragments.CurrentLocationFragment;

@Component(modules = CacheModule.class)
public interface CurrentLocationComponent { // Must be public
	void inject(CurrentLocationFragment currentLocationFragment);
}
