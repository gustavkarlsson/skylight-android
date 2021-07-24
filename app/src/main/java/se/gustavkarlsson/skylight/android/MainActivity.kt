package se.gustavkarlsson.skylight.android

import android.content.Intent
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
import se.gustavkarlsson.skylight.android.lib.navigation.BackPressHandler
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackChange
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.navigation.topScreen
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.places.getPlaceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceClearer
import se.gustavkarlsson.skylight.android.lib.ui.ScopeHost

// TODO Inject things
internal class MainActivity :
    AppCompatActivity(),
    ScopeHost {

    private val serviceClearer: ServiceClearer = ScopedServiceComponent.instance.serviceClearer()

    private val selectedPlaceRepository: SelectedPlaceRepository = PlacesComponent.instance.selectedPlaceRepository()

    private val navigator: Navigator = NavigationComponent.instance.navigator()

    private val backPressHandler: BackPressHandler = NavigationComponent.instance.backPressHandler()

    private val renderer: Renderer by lazy {
        Renderer(this, NavigationComponent.instance.navigator(), serviceClearer)
    }

    // Create Destroy
    override var createDestroyScope: CoroutineScope? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (savedInstanceState == null && navigator.topScreen == null) {
            navigator.goTo(screens.main)
        }
        val scope = createLifecycleScope("createDestroyScope")
        createDestroyScope = scope
        onEachScreen { onCreateDestroyScope(scope) }
        scope.launch { navigator.backstackChanges.collect(::onBackstackChange) }
        intent?.getPlaceId()?.let { placeId ->
            onNewPlaceId(placeId)
        }
        renderer.render()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getPlaceId()?.let { placeId ->
            onNewPlaceId(placeId)
        }
    }

    private fun onNewPlaceId(placeId: PlaceId) {
        selectedPlaceRepository.set(placeId)
        navigator.setBackstack(listOf(screens.main)) // FIXME Inject instead?
    }

    // TODO Implement listeners instead?
    private fun onBackstackChange(change: BackstackChange) {
        passScopesToNewScreens(change)
        trackScreenChange(change)
        finishIfEmpty(change)
    }

    private fun passScopesToNewScreens(change: BackstackChange) {
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
    }

    private fun List<Screen>.tryPassScope(scope: CoroutineScope?, action: Screen.(CoroutineScope) -> Unit) {
        if (scope == null) return
        for (element in this) {
            element.action(scope)
        }
    }

    private fun trackScreenChange(change: BackstackChange) {
        val oldTop = change.old.lastOrNull()
        val newTop = change.new.lastOrNull()
        if (newTop != null && oldTop != newTop) {
            AnalyticsComponent.instance.analytics().logScreen(newTop.name.name)
        }
    }

    private fun finishIfEmpty(change: BackstackChange) {
        if (change.new.isEmpty()) {
            finish()
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

    override fun onPause() {
        resumePauseScope?.cancel("onPause called")
        resumePauseScope = null
        super.onPause()
    }

    override fun onBackPressed() = backPressHandler.onBackPress()

    private fun createLifecycleScope(name: String) = MainScope() + CoroutineName(name)

    private fun onEachScreen(block: Screen.() -> Unit) {
        val change = navigator.backstackChanges.value
        change.new.forEach { screen ->
            screen.block()
        }
    }
}
