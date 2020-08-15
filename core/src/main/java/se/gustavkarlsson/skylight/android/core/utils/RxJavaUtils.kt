package se.gustavkarlsson.skylight.android.core.utils

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import java.util.concurrent.TimeUnit

fun <T : Any> Single<T>.delay(delay: Duration): Single<T> =
    this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Observable<T>.delay(delay: Duration): Observable<T> =
    this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Flowable<T>.delay(delay: Duration): Flowable<T> =
    this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Single<T>.timeout(timeout: Duration): Single<T> =
    this.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Observable<T>.debounce(interval: Duration): Observable<T> =
    this.debounce(interval.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Observable<T>.throttleLatest(duration: Duration): Observable<T> =
    this.throttleLatest(duration.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Single<T>.delaySubscription(delay: Duration): Single<T> =
    this.delaySubscription(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any> Observable<T>.buffer(delay: Duration): Observable<List<T>> =
    this.buffer(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T : Any, R : Any> Observable<T>.mapNotNull(mapper: (T) -> R?): Observable<R> =
    this.flatMapMaybe {
        val returned = mapper(it)
        if (returned == null) {
            Maybe.empty()
        } else {
            Maybe.just(returned)
        }
    }
