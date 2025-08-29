package se.gustavkarlsson.skylight.android.feature.intro

import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.ViewModelScope
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import se.gustavkarlsson.skylight.android.lib.ui.CoroutineScopedService

@Inject
@ViewModelScope
internal class IntroViewModel(
    private val versionManager: RunVersionManager,
) : CoroutineScopedService() {
    fun registerScreenSeen(target: Backstack) {
        scope.launch {
            versionManager.signalRunCompleted()
            navigator.setBackstack(target)
        }
    }
}
