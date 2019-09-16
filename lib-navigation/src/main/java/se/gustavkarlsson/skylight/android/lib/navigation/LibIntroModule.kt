package se.gustavkarlsson.skylight.android.lib.navigation

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import org.koin.dsl.module.module

private typealias SSNavigator = com.zhuinden.simplestack.navigator.Navigator

val libNavigationModule = module {

	scope<Navigator>("activity") {
		val activity = get<Activity>(scopeId = "activity")
		val fragmentManager = get<FragmentManager>(scopeId = "activity")
		val container = get<ViewGroup>(scopeId = "activity")
		val backstack = SSNavigator
			.configure()
			.setStateChanger(FragmentStateChanger(fragmentManager, container.id))
			.install(activity, container, listOf(NavItem.EMPTY))
		SimpleStackNavigator(backstack)
	}

	single<FragmentFactoryDirectory> {
		FragmentFactoryDirectoryImpl()
	}

}
