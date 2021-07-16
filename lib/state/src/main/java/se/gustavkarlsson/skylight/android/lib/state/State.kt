package se.gustavkarlsson.skylight.android.lib.state

class State private constructor(private val map: Map<Any, Any?>) {
    @Suppress("UNCHECKED_CAST")
    operator fun <S> get(key: StateKey<S>): S {
        return when (val value = map[key]) {
            null -> key.createState(this)
            else -> value as S
        }
    }

    fun <S> copy(key: StateKey<S>, value: S): State {
        val mutableMap = map.toMutableMap()
        mutableMap[key] = value
        return State(mutableMap)
    }
}

interface StateKey<S> {
    fun createState(state: State): S
}
