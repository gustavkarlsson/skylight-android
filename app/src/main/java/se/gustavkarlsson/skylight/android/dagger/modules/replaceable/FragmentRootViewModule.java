package se.gustavkarlsson.skylight.android.dagger.modules.replaceable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope;

@Module
public class FragmentRootViewModule {
	public static final String FRAGMENT_ROOT_NAME = "FragmentRoot";

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
