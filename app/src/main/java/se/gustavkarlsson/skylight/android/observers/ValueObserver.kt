package se.gustavkarlsson.skylight.android.observers

interface ValueObserver<in T> {
    fun valueChanged(newData: T)
}
