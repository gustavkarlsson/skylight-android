package se.gustavkarlsson.skylight.android.feature.googleplayservices

import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactory
import se.gustavkarlsson.skylight.android.lib.navigation.FragmentFactoryRegistry
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.NavItemOverride
import se.gustavkarlsson.skylight.android.lib.navigation.NavItemOverrideRegistry

val featureGooglePlayServicesModule = module {

    single<GooglePlayServicesChecker> {
        GmsGooglePlayServicesChecker(context = get())
    }

    viewModel { (destination: NavItem) ->
        GooglePlayServicesViewModel(
            navigator = get(),
            destination = destination
        )
    }

    single<ModuleStarter>("googleplayservices") {
        val googlePlayServicesChecker = get<GooglePlayServicesChecker>()
        object : ModuleStarter {
            override fun start() {
                val override = object : NavItemOverride {
                    override val priority: Int = 10
                    override fun override(item: NavItem): NavItem? =
                        if (!googlePlayServicesChecker.isAvailable) {
                            NavItem("googleplayservices") { "destination" to item }
                        } else null
                }
                get<NavItemOverrideRegistry>().register(override)

                val fragmentFactory = object : FragmentFactory {
                    override fun createFragment(name: String): Fragment? =
                        if (name == "googleplayservices") GooglePlayServicesFragment()
                        else null
                }
                get<FragmentFactoryRegistry>().register(fragmentFactory)
            }
        }
    }
}
