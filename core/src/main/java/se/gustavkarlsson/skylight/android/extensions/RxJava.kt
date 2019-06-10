package se.gustavkarlsson.skylight.android.extensions

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import java.util.concurrent.TimeUnit

fun <T : Any> Single<T>.delay(delay: Duration): Single<T> =
	this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Flowable<T>.delay(delay: Duration): Flowable<T> =
	this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Observable<T>.delay(delay: Duration): Observable<T> =
	this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Single<T>.timeout(timeout: Duration): Single<T> =
	this.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Flowable<T>.debounce(interval: Duration): Flowable<T> =
	this.debounce(interval.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Single<T>.delaySubscription(delay: Duration): Single<T> =
	this.delaySubscription(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any, R : Any> Flowable<T>.mapNotNull(mapper: (T) -> R?): Flowable<R> =
	this.flatMapMaybe {
		val returned = mapper(it)
		if (returned == null) {
			Maybe.empty()
		} else {
			Maybe.just(returned)
		}
	}

fun <T : Any, R : Any> Observable<T>.mapNotNull(mapper: (T) -> R?): Observable<R> =
	this.flatMapMaybe {
		val returned = mapper(it)
		if (returned == null) {
			Maybe.empty()
		} else {
			Maybe.just(returned)
		}
	}
