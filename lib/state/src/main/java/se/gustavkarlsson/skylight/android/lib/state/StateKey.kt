package se.gustavkarlsson.skylight.android.lib.state

interface StateKey<S> {
    fun createState(state: State): S
}
