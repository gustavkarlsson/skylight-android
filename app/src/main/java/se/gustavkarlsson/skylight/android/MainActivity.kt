package se.gustavkarlsson.skylight.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigatorHost
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.Screens
import se.gustavkarlsson.skylight.android.lib.navigation.ScreensHost
import se.gustavkarlsson.skylight.android.lib.navigationsetup.MasterNavigator
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

    override val screens: Screens = DefaultScreens

    override val navigator: MasterNavigator by lazy {
        val installer = NavigationSetupComponent.create().navigationInstaller()
        installer.install(
            activity = this,
            initialBackstack = listOf(screens.main),
            navigationOverrides = NavigationComponent.instance.navigationOverrides(),
        )
    }

    private val renderer: Renderer by lazy {
        Renderer(this, navigator)
    }

    // Create Destroy
    override var createDestroyScope: CoroutineScope? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val scope = createLifecycleScope("createDestroyScope")
        createDestroyScope = scope
        onEachScreen { onCreateDestroyScope(scope) }
        scope.launch { handleBackstackChanges() }
        renderer.render()
    }

    private suspend fun handleBackstackChanges() {
        navigator.backstackChanges.collect { change ->
            val oldTop = change.old.lastOrNull()
            val newTop = change.new.lastOrNull()
            if (newTop != null && oldTop != newTop) {
                AnalyticsComponent.instance.analytics().logScreen(newTop.name.name)
            }
            val tags = change.new.map(Screen::tag)
            serviceRegistry.onTagsChanged(tags)
            val addedScreens = change.new - change.old
            addedScreens.tryPassScope(createDestroyScope) { scope ->
                onCreateDestroyScope(scope)
            }
            addedScreens.tryPassScope(startStopScope) { scope ->
                onStartStopScope(scope)
            }
            addedScreens.tryPassScope(resumePauseScope) { scope ->
                onResumePauseScope(scope)
            }
            if (change.new.isEmpty()) {
                finish()
            }
        }
    }

    private fun List<Screen>.tryPassScope(scope: CoroutineScope?, action: Screen.(CoroutineScope) -> Unit) {
        if (scope == null) return
        for (element in this) {
            element.action(scope)
        }
    }

    override fun onDestroy() {
        createDestroyScope?.cancel("onDestroy called")
        createDestroyScope = null
        super.onDestroy()
    }

    // Start Stop
    override var startStopScope: CoroutineScope? = null
        private set

    override fun onStart() {
        super.onStart()
        val scope = createLifecycleScope("startStopScope")
        startStopScope = scope
        onEachScreen { onStartStopScope(scope) }
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
        val scope = createLifecycleScope("resumePauseScope")
        resumePauseScope = scope
        onEachScreen { onResumePauseScope(scope) }
    }

    private fun createLifecycleScope(name: String) = MainScope() + CoroutineName(name)

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

    override fun onBackPressed() = navigator.onBackPress(this)
}
