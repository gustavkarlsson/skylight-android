package se.gustavkarlsson.skylight.android.entities

sealed class Loadable<out T> {
    abstract fun <R> map(mapper: (T) -> R): Loadable<R>

    object Loading : Loadable<Nothing>() {
        override fun <R> map(mapper: (Nothing) -> R): Loadable<R> = Loading
    }

    data class Loaded<out T>(val value: T) : Loadable<T>() {
        override fun <R> map(mapper: (T) -> R): Loadable<R> = Loaded(mapper(value))
    }

    companion object {
        fun <T> loading(): Loadable<T> = Loading
        fun <T> loaded(value: T): Loadable<T> = Loaded(value)
    }
}
