package se.gustavkarlsson.skylight.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.navigation.BackPress
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackChange
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.NavigatorHost
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.Screens
import se.gustavkarlsson.skylight.android.lib.navigation.ScreensHost
import se.gustavkarlsson.skylight.android.lib.navigationsetup.NavigationSetupComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceCatalog
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceHost
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceRegistry
import se.gustavkarlsson.skylight.android.lib.ui.ScopeHost
import se.gustavkarlsson.skylight.android.navigation.DefaultScreens

// TODO Can some of this be moved to navigationsetup?
internal class MainActivity :
    AppCompatActivity(),
    NavigatorHost,
    ScreensHost,
    ScopeHost,
    ServiceHost {

    private val serviceRegistry: ServiceRegistry = ScopedServiceComponent.instance.serviceRegistry()

    override val serviceCatalog: ServiceCatalog get() = serviceRegistry

    override val navigator: Navigator by lazy {
        val installer = NavigationSetupComponent.instance.navigationInstaller()
        installer.install(
            activity = this,
            initialBackstack = listOf(screens.main),
            navigationOverrides = NavigationComponent.instance.navigationOverrides(),
        )
    }

    override val screens: Screens = DefaultScreens

    // Create Destroy
    override var createDestroyScope: CoroutineScope? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val scope = MainScope() + CoroutineName("createDestroyScope")
        createDestroyScope = scope
        setContent()
        onEachScreen { onNewCreateDestroyScope(scope) }
        scope.handleBackstackChanges()
    }

    override fun onDestroy() {
        createDestroyScope?.cancel("onDestroy called")
        createDestroyScope = null
        super.onDestroy()
    }

    private fun setContent() = setContent {
        val change by navigator.backstackChanges.collectAsState()
        val topScreen = change.new.lastOrNull()
        Crossfade(targetState = topScreen) { screen -> // TODO different animations for different directions?
            screen?.run { Content() }
        }
    }

    private fun CoroutineScope.handleBackstackChanges() = launch {
        navigator.backstackChanges.collect { change ->
            onBackstackChange(change)
        }
    }

    private fun onBackstackChange(change: BackstackChange) {
        val oldTop = change.old.lastOrNull()
        val newTop = change.new.lastOrNull()
        if (newTop != null && oldTop != newTop) {
            AnalyticsComponent.instance.analytics().logScreen(newTop.name.name)
        }
        val tags = change.new.map(Screen::tag)
        serviceRegistry.onTagsChanged(tags)
        if (change.new.isEmpty()) {
            finish()
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

    private fun onEachScreen(block: Screen.() -> Unit) {
        val change = navigator.backstackChanges.value
        change.new.forEach { screen ->
            screen.block()
        }
    }

    override fun onBackPressed() {
        val topScreen = navigator.backstackChanges.value.new.lastOrNull()
        val backPress = topScreen?.run {
            onBackPress()
        }
        when (backPress) {
            null -> logInfo { "Top screen could not handle back press" }
            BackPress.HANDLED -> logInfo { "Top screen handled back press" }
            BackPress.NOT_HANDLED -> logInfo { "Top screen did not handle back press" }
        }
        if (backPress != BackPress.HANDLED) navigator.closeScreen()
    }
}
