package se.gustavkarlsson.skylight.android

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.zachklipp.compose.backstack.Backstack
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
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
            val backstack = change.new.takeIf { it.isNotEmpty() } ?: change.old
            Backstack(
                backstack = backstack,
                transition = CrossFadeZoom,
            ) { screen ->
                screen.run { activity.Content(scope) }
            }
        }
    }
}
