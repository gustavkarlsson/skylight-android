package se.gustavkarlsson.skylight.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.navigation.BackPressHandler
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
    internal lateinit var backPressHandler: BackPressHandler

    @Inject
    internal lateinit var screens: Screens

    init {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                navigator.leave.collect {
                    // TODO This doesn't seem to be recommended anymore
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MainActivityComponent.instance.inject(this)
        intent?.getPlaceId()?.let { placeId ->
            onNewPlaceId(placeId)
        }
        renderer.render(this)
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

    // TODO This doesn't seem to be recommended anymore
    @Suppress("OVERRIDE_DEPRECATION")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() = backPressHandler.onBackPress()
}
