package se.gustavkarlsson.skylight.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.navigation.BackPressHandler
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screens
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.lib.places.getPlaceId
import javax.inject.Inject

internal class MainActivity :
    AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var renderer: Renderer

    @Inject
    lateinit var selectedPlaceRepository: SelectedPlaceRepository

    @Inject
    lateinit var backPressHandler: BackPressHandler

    @Inject
    lateinit var screens: Screens

    init {
        lifecycleScope.launch {
            while (true) {
                lifecycle.whenCreated {
                    navigator.leave.collect {
                        super.onBackPressed()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        MainActivityComponent.instance.inject(this)
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
        navigator.setBackstack(Backstack(screens.main))
    }

    override fun onBackPressed() = backPressHandler.onBackPress()
}
