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
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.lib.navigation.BackPressHandler
import se.gustavkarlsson.skylight.android.lib.navigation.BackstackChange
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.Screens
import se.gustavkarlsson.skylight.android.lib.navigation.topScreen
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.places.getPlaceId
import se.gustavkarlsson.skylight.android.lib.ui.ScopeHost
import javax.inject.Inject

internal class MainActivity :
    AppCompatActivity(),
    ScopeHost {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var renderer: Renderer

    @Inject
    lateinit var selectedPlaceRepository: SelectedPlaceRepository

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var backPressHandler: BackPressHandler

    @Inject
    lateinit var screens: Screens

    // Create Destroy
    override var createDestroyScope: CoroutineScope? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        MainActivityComponent.instance.inject(this)
        if (savedInstanceState == null && navigator.topScreen == null) {
            navigator.goTo(screens.main)
        }
        val scope = createLifecycleScope("createDestroyScope")
        createDestroyScope = scope
        onEachScreen { onCreateDestroyScope(this@MainActivity, scope) }
        scope.launch { navigator.backstackChanges.collect(::onBackstackChange) }
        intent?.getPlaceId()?.let { placeId ->
            onNewPlaceId(placeId)
        }
        renderer.render(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getPlaceId()?.let { placeId ->
            onNewPlaceId(placeId)
        }
    }

    private fun onNewPlaceId(placeId: PlaceId) {
        selectedPlaceRepository.set(placeId)
        navigator.setBackstack(listOf(screens.main))
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
            onCreateDestroyScope(this@MainActivity, scope)
        }
        addedScreens.tryPassScope(startStopScope) { scope ->
            onStartStopScope(this@MainActivity, scope)
        }
        addedScreens.tryPassScope(resumePauseScope) { scope ->
            onResumePauseScope(this@MainActivity, scope)
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
            analytics.logScreen(newTop.type.name)
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
        onEachScreen { onStartStopScope(this@MainActivity, scope) }
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
        onEachScreen { onResumePauseScope(this@MainActivity, scope) }
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
