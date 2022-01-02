package se.gustavkarlsson.skylight.android

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceClearer
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.collectAsLifecycleAwareState
import javax.inject.Inject

internal class Renderer @Inject constructor(
    private val navigator: Navigator,
    private val serviceClearer: ServiceClearer,
) {
    fun render(activity: AppCompatActivity) {
        activity.setContent {
            val change by navigator.backstackChanges.collectAsLifecycleAwareState()
            RenderCrossfade(activity, change.new.screens.last()) {
                val removedScreens = change.old.screens - change.new.screens
                val tagsToClear = removedScreens.map { it.toTag() }
                serviceClearer.clear(tagsToClear)
            }
        }
    }

    @Composable
    private fun RenderCrossfade(
        activity: AppCompatActivity,
        screen: Screen,
        onDispose: () -> Unit,
    ) {
        Crossfade(targetState = screen) { renderingScreen ->
            val tag = renderingScreen.toTag()
            DisposableEffect(key1 = tag) {
                onDispose(onDispose)
            }
            renderingScreen.Content(activity, tag)
        }
    }
}

private fun Screen.toTag(): ServiceTag = ServiceTag("$type[${hashCode()}]")
