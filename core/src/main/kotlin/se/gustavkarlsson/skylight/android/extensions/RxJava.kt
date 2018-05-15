package se.gustavkarlsson.skylight.android.extensions

import io.reactivex.functions.Consumer

operator fun <T> Consumer<T>.invoke(value: T) = this.accept(value)
operator fun Consumer<Unit>.invoke() = this.accept(Unit)
