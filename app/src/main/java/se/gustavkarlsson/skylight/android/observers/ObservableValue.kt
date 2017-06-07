package se.gustavkarlsson.skylight.android.observers

import java.util.*
import java.util.Collections.newSetFromMap
import kotlin.properties.Delegates

internal class ObservableValue<T>(initialValue: T) {
	var value: T by Delegates.observable(initialValue) {
		_, _, new -> listeners.forEach { it.valueChanged(new) }
	}

	private val listeners = newSetFromMap(IdentityHashMap<ValueObserver<T>, Boolean>(20))

    fun addListener(listener: ValueObserver<T>): Boolean {
        return listeners.add(listener)
    }

    fun removeListener(listener: ValueObserver<T>): Boolean {
        return listeners.remove(listener)
    }

}
