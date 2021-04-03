package se.gustavkarlsson.skylight.android.lib.ui

import kotlinx.coroutines.CoroutineScope

interface ScopeHost {
    val createDestroyScope: CoroutineScope?
    val startStopScope: CoroutineScope?
    val resumePauseScope: CoroutineScope?
}
