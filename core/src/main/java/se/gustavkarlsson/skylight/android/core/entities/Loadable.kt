package se.gustavkarlsson.skylight.android.core.entities

// TODO Move map to extension?
sealed class Loadable<out T> {
    abstract val value: T?
    abstract fun <R> map(mapper: (T) -> R): Loadable<R>

    object Loading : Loadable<Nothing>() {
        override fun <R> map(mapper: (Nothing) -> R): Loadable<R> = Loading
        override val value: Nothing? = null
    }

    data class Loaded<out T>(override val value: T) : Loadable<T>() {
        override fun <R> map(mapper: (T) -> R): Loadable<R> = Loaded(mapper(value))
    }

    // TODO Remove?
    companion object {
        fun <T> loading(): Loadable<T> = Loading
        fun <T> loaded(value: T): Loadable<T> = Loaded(value)
    }
}
