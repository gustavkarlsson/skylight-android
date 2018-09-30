package se.gustavkarlsson.skylight.android.extensions

import android.content.ComponentCallbacks
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

inline fun <reified T : Any> ComponentCallbacks.addToKoin(value: T) {
	inject<T> { parametersOf(value) }.value
}
