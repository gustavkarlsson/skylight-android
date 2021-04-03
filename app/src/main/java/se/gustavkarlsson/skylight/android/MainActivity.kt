package se.gustavkarlsson.skylight.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackListener
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.NavigatorHost
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.Screens
import se.gustavkarlsson.skylight.android.lib.navigation.ScreensHost
import se.gustavkarlsson.skylight.android.lib.navigationsetup.BackButtonController
import se.gustavkarlsson.skylight.android.lib.navigationsetup.NavigationSetupComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceCatalog
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceHost
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceRegistry
import se.gustavkarlsson.skylight.android.lib.ui.ScopeHost
import se.gustavkarlsson.skylight.android.navigation.DefaultScreens

internal class MainActivity :
    AppCompatActivity(),
    NavigatorHost,
    ScreensHost,
    ScopeHost,
    ServiceHost,
    BackstackListener {

    override lateinit var navigator: Navigator private set

    private lateinit var backButtonController: BackButtonController

    private val serviceRegistry: ServiceRegistry = ScopedServiceComponent.instance.serviceRegistry()

    override val serviceCatalog: ServiceCatalog get() = serviceRegistry

    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    override val screens: Screens = DefaultScreens

    // Create Destroy
    override var createDestroyScope: CoroutineScope? = null
        private set

    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val scope = MainScope() + CoroutineName("createDestroyScope")
        createDestroyScope = scope
        setupNavigation()
        setContent()
        onEachScreen { onNewCreateDestroyScope(scope) }
    }

    override fun onDestroy() {
        createDestroyScope?.cancel("onDestroy called")
        createDestroyScope = null
        super.onDestroy()
    }

    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    private fun setupNavigation() {
        val installer = NavigationSetupComponent.instance.navigationInstaller()
        val (navigator, backButtonController) = installer.install(
            activity = this,
            initialBackstack = listOf(screens.main),
            navigationOverrides = NavigationComponent.instance.navigationOverrides(),
            backstackListeners = listOf(this),
        )
        this.navigator = navigator
        this.backButtonController = backButtonController
    }

    private fun setContent() = setContent {
        val backstack by navigator.backstack.observeAsState()
        val topScreen = backstack?.lastOrNull()
        Crossfade(targetState = topScreen) { screen -> // TODO different animations for different directions?
            screen?.run { Content() }
        }
    }

    // Start Stop
    override var startStopScope: CoroutineScope? = null
        private set

    override fun onStart() {
        super.onStart()
        val scope = MainScope() + CoroutineName("startStopScope")
        startStopScope = scope
        onEachScreen { onNewStartStopScope(scope) }
    }

    override fun onStop() {
        startStopScope?.cancel("onStop called")
        startStopScope = null
        super.onStop()
    }

    // Resume Pause
    override var resumePauseScope: CoroutineScope? = null
        private set

    override fun onResume() {
        super.onResume()
        val scope = MainScope() + CoroutineName("resumePauseScope")
        resumePauseScope = scope
        onEachScreen { onNewResumePauseScope(scope) }
    }

    override fun onPause() {
        resumePauseScope?.cancel("onPause called")
        resumePauseScope = null
        super.onPause()
    }

    override fun onBackstackChanged(old: Backstack, new: Backstack) {
        val oldTop = old.lastOrNull()
        val newTop = new.lastOrNull()
        if (newTop != null && oldTop != newTop) {
            AnalyticsComponent.instance.analytics().logScreen(newTop.name.name)
        }
        val tags = new.map(Screen::tag)
        serviceRegistry.onTagsChanged(tags)
        if (new.isEmpty()) {
            finish()
        }
    }

    override fun onBackPressed() = backButtonController.onBackPress()

    private fun onEachScreen(block: Screen.() -> Unit) {
        navigator.backstack.value?.forEach { screen ->
            screen.block()
        }
    }
}
