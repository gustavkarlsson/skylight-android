package se.gustavkarlsson.skylight.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.NavigatorHost
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreensHost
import se.gustavkarlsson.skylight.android.lib.navigationsetup.BackButtonController
import se.gustavkarlsson.skylight.android.lib.navigationsetup.NavigationInstaller
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceHost
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceRegistry

internal class MainActivity : AppCompatActivity(), NavigatorHost, ScreensHost, ServiceHost,
    BackstackListener {

    override lateinit var navigator: Navigator private set

    private lateinit var backButtonController: BackButtonController

    override val serviceRegistry: ServiceRegistry = get()

    override val screens = DefaultScreens

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation() {
        val (navigator, backButtonController) = get<NavigationInstaller>().install(
            this,
            R.id.fragmentContainer,
            listOf(screens.main),
            listOf(get("intro"), get("googleplayservices")),
            emptyList(),
            animationConfig
        )
        this.navigator = navigator
        this.backButtonController = backButtonController
    }

    override fun finish() {
        serviceRegistry.clear()
        super.finish()
    }

    override fun onBackstackChanged(old: Backstack, new: Backstack) {
        val tags = new.map(Screen::tag)
        serviceRegistry.onTagsChanged(tags)
    }

    override fun onBackPressed() = backButtonController.onBackPressed()
}
