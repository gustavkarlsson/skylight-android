package se.gustavkarlsson.skylight.android.core

import kotlinx.coroutines.CoroutineScope

// TODO Make functional interface
interface ModuleStarter {
    fun start(scope: CoroutineScope)
}
