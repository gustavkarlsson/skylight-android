package se.gustavkarlsson.skylight.android

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.zachklipp.compose.backstack.Backstack
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.ui.compose.collectAsLifecycleAwareState
import se.gustavkarlsson.skylight.android.transitions.CrossFadeZoom

internal class Renderer(
    private val activity: AppCompatActivity,
    private val navigator: Navigator,
) {
    fun render() {
        activity.setContent {
            val scope = rememberCoroutineScope {
                SupervisorJob() + Dispatchers.Main + CoroutineName("viewScope")
            }
            val change by navigator.backstackChanges.collectAsLifecycleAwareState()
            val topScreen = change.new.lastOrNull()
            RenderSimple(topScreen, scope)
        }
    }

    // FIXME Does not render when the last screen is removed. Screen turns blank while it animates to background
    @Composable
    private fun RenderSimple(screen: Screen?, scope: CoroutineScope) {
        screen?.run { activity.Content(scope) }
    }

    // TODO This doesn't work. When screen has changed, the old screen will still re-fetch the service and start over while fading out
    @Composable
    private fun RenderCrossfade(screen: Screen?, scope: CoroutineScope) {
        Crossfade(targetState = screen) { screenToRender ->
            screenToRender?.run { activity.Content(scope) }
        }
    }

    // TODO Backstack crashes when navigating to GooglePlayServicesScreen
    //  Key -35963410 was used multiple times
    //  Does tih also have the same problem as crossfade?
    @Composable
    private fun RenderBackstack(backstack: Backstack, scope: CoroutineScope) {
        Backstack(
            backstack = backstack,
            transition = CrossFadeZoom,
        ) { screen ->
            screen.run { activity.Content(scope) }
        }
    }
}
