package se.gustavkarlsson.skylight.android.observers

internal interface ValueObserver<in T> {
    fun valueChanged(newData: T)
}
