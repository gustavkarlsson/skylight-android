package se.gustavkarlsson.skylight.android.lib.navigation

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import org.koin.dsl.module.module

typealias SSNavigator = com.zhuinden.simplestack.navigator.Navigator

val libNavigationModule = module {

	scope<Navigator>("activity") {
		val fragmentManager = get<FragmentManager>(scopeId = "activity")
		val fragmentFactory = get<FragmentFactory>()
		val container = get<ViewGroup>(scopeId = "activity")
		val activity = get<Activity>(scopeId = "activity")
		val navItemOverride = get<NavItemOverride>()
		val stateChanger = FragmentStateChanger(
			fragmentManager,
			fragmentFactory,
			container.id
		)
		val backstack = SSNavigator
			.configure()
			.setStateChanger(stateChanger)
			.install(activity, container, listOf(NavItem.EMPTY))
		SimpleStackNavigator(activity, fragmentManager, backstack, navItemOverride)
	}

	single {
		FragmentFactoryRegistryImpl()
	}

	single<FragmentFactoryRegistry> {
		get<FragmentFactoryRegistryImpl>()
	}

	single<FragmentFactory> {
		get<FragmentFactoryRegistryImpl>()
	}

	single {
		NavItemOverrideRegistryImpl()
	}

	single<NavItemOverrideRegistry> {
		get<NavItemOverrideRegistryImpl>()
	}

	single<NavItemOverride> {
		get<NavItemOverrideRegistryImpl>()
	}
}
