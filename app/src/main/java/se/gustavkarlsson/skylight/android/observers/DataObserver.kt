package se.gustavkarlsson.skylight.android.observers

interface DataObserver<in T> {
    fun dataChanged(newData: T)
}
