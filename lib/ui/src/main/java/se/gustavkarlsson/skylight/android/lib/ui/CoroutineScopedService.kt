package se.gustavkarlsson.skylight.android.lib.ui

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService

abstract class CoroutineScopedService : ScopedService {
    protected val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineName(javaClass.simpleName)
    )

    @CallSuper
    override fun onCleared() {
        scope.cancel("${javaClass.simpleName} cleared")
    }
}
