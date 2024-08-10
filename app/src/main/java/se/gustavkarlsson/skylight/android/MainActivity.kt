package se.gustavkarlsson.skylight.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screens
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.places.getPlaceId
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    internal lateinit var navigator: Navigator

    @Inject
    internal lateinit var renderer: Renderer

    @Inject
    internal lateinit var selectedPlaceRepository: SelectedPlaceRepository

    @Inject
    internal lateinit var screens: Screens

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MainActivityComponent.instance.inject(this)
        setupBackPressHandling()
        intent?.getPlaceId()?.let { placeId ->
            onNewPlaceId(placeId)
        }
        renderer.render(this)
    }

    private fun setupBackPressHandling() {
        val callback = object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                logDebug { "Back press callback invoked. Closing screen." }
                navigator.closeScreen()
            }
        }
        // Ensure the callback intercepts back button presses when there is more than one screen on the back stack.
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                navigator.backstackChanges
                    .map { it.new.screens.size > 1 }
                    .collect { hasMultipleScreens ->
                        if (hasMultipleScreens && !onBackPressedDispatcher.hasEnabledCallbacks()) {
                            onBackPressedDispatcher.addCallback(callback)
                            logDebug { "Multiple screens on backstack. Adding back-press callback." }
                        } else if (!hasMultipleScreens && onBackPressedDispatcher.hasEnabledCallbacks()) {
                            callback.remove()
                            logDebug { "Only one screen left on backstack. Removing back-press callback." }
                        }
                    }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getPlaceId()?.let { placeId ->
            onNewPlaceId(placeId)
        }
    }

    private var onNewPlaceJob: Job? = null
    private fun onNewPlaceId(placeId: PlaceId) {
        onNewPlaceJob?.cancel()
        onNewPlaceJob = lifecycleScope.launch {
            selectedPlaceRepository.set(placeId)
            navigator.setBackstack(Backstack(screens.main))
        }
    }
}
