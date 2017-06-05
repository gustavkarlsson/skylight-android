package se.gustavkarlsson.skylight.android.observers

import java.util.*
import java.util.Collections.newSetFromMap

class ObservableData<T>(data: T) {
	var data: T = data
	set(data) {
        field = data
		listeners.forEach { it.dataChanged(data) }
    }

	private val listeners = newSetFromMap(IdentityHashMap<DataObserver<T>, Boolean>(20))

    fun addListener(listener: DataObserver<T>): Boolean {
        return listeners.add(listener)
    }

    fun removeListener(listener: DataObserver<T>): Boolean {
        return listeners.remove(listener)
    }

}
