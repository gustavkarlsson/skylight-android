package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.ui.Destination
import se.gustavkarlsson.skylight.android.lib.ui.DestinationRegistry
import se.gustavkarlsson.skylight.android.gui.screens.main.MainFragment

val mainModule = module {

	single<ModuleStarter>("main") {
		object : ModuleStarter {
			override fun start() {
				val destination = Destination("main", 0, true) { id ->
					if (id == "main")
						MainFragment()
					else
						null
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}

}
