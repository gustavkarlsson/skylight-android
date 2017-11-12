package se.gustavkarlsson.skylight.android.extensions

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observe: (T?) -> Unit) {
	observe(owner, Observer {
		observe(it)
	})
}
