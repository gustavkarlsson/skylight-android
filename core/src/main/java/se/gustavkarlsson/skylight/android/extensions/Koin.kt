package se.gustavkarlsson.skylight.android.extensions

import android.content.ComponentCallbacks
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

inline fun <reified T : Any> ComponentCallbacks.addToKoin(value: T, name: String = "", scope: Scope? = null) {
	inject<T>(name = name, scope = scope) { parametersOf(value) }.value
}
