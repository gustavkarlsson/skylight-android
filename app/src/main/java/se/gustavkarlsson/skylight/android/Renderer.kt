package se.gustavkarlsson.skylight.android

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceClearer
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.collectAsLifecycleAwareState
import se.gustavkarlsson.skylight.android.transitions.CrossFadeZoom

internal class Renderer(
    private val activity: AppCompatActivity,
    private val navigator: Navigator,
    private val serviceClearer: ServiceClearer,
) {
    fun render() {
        activity.setContent {
            val scope = rememberCoroutineScope {
                SupervisorJob() + Dispatchers.Main + CoroutineName("viewScope")
            }
            val change by navigator.backstackChanges.collectAsLifecycleAwareState()
            RenderCrossfade(scope, change.new.lastOrNull()) {
                val removedScreens = change.old - change.new
                val tagsToClear = removedScreens.map { it.toTag() }
                serviceClearer.clear(tagsToClear)
            }
        }
    }

    @Composable
    private fun RenderCrossfade(scope: CoroutineScope, screen: Screen?, onDispose: () -> Unit) {
        Crossfade(targetState = screen) { renderingScreen ->
            if (renderingScreen != null) {
                val tag = renderingScreen.toTag()
                DisposableEffect(key1 = tag) {
                    onDispose(onDispose)
                }
                renderingScreen.run { activity.Content(tag, scope) }
            }
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
            screen.run {
                val tag = screen.toTag()
                activity.Content(tag, scope)
            }
        }
    }
}

private fun Screen.toTag(): ServiceTag = ServiceTag(id)
