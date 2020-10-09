package se.gustavkarlsson.skylight.android.core

import kotlinx.coroutines.CoroutineScope

fun interface ModuleStarter {
    fun start(scope: CoroutineScope)
}
