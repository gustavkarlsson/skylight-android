package se.gustavkarlsson.skylight.android.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

fun Disposable.addTo(compositeDisposable: CompositeDisposable) = compositeDisposable.add(this)

operator fun <T> Consumer<T>.invoke(value: T) = this.accept(value)
operator fun Consumer<Unit>.invoke() = this.accept(Unit)
