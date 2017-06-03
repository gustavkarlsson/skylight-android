package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;

import static se.gustavkarlsson.skylight.android.dagger.Names.FRAGMENT_ROOT_NAME;

@Module
public class FragmentRootViewModule {

	private final LayoutInflater inflater;
	private final ViewGroup container;
	private final int fragmentId;

	public FragmentRootViewModule(LayoutInflater inflater, ViewGroup container, int fragmentId) {
		this.inflater = inflater;
		this.container = container;
		this.fragmentId = fragmentId;
	}

	// Published
	@Provides
	@FragmentScope
	@Named(FRAGMENT_ROOT_NAME)
	View provideView() {
		return inflater.inflate(fragmentId, container, false);
	}
}
