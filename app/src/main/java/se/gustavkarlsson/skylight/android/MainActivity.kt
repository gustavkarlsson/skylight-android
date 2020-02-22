package se.gustavkarlsson.skylight.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import se.gustavkarlsson.skylight.android.lib.navigation.newer.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.newer.NavigatorHost
import se.gustavkarlsson.skylight.android.lib.navigation.newer.ScreensHost
import se.gustavkarlsson.skylight.android.lib.navigationsetup.BackButtonController
import se.gustavkarlsson.skylight.android.lib.navigationsetup.NavigationInstaller

internal class MainActivity : AppCompatActivity(), NavigatorHost, ScreensHost {

    override lateinit var navigator: Navigator private set

    private lateinit var backButtonController: BackButtonController

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

    override fun onBackPressed() = backButtonController.onBackPressed()
}
