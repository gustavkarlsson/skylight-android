package se.gustavkarlsson.skylight.android.lib.state

class State private constructor(private val map: Map<Any, Any>) {
    @Suppress("UNCHECKED_CAST")
    operator fun <S> get(key: StateKey<S>): S {
        return when (val value = map[key]) {
            null -> key.createState(this)
            else -> value as S
        }
    }

    fun update(state: State): S {

    }
}

interface StateKey<S> {
    fun createState(state: State): S
}
