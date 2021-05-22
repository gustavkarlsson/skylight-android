package se.gustavkarlsson.skylight.android.lib.ui

import android.app.Activity
import kotlinx.coroutines.CoroutineScope

val Activity.createDestroyScope: CoroutineScope?
    get() = scopes.createDestroyScope

val Activity.startStopScope: CoroutineScope?
    get() = scopes.startStopScope

val Activity.resumePauseScope: CoroutineScope?
    get() = scopes.resumePauseScope

private val Activity.scopes: ScopeHost
    get() = this as? ScopeHost ?: error("Activity does not implement ${ScopeHost::class.java.name}")
