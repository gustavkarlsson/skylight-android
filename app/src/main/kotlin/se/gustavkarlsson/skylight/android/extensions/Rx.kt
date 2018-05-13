package se.gustavkarlsson.skylight.android.extensions

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import java.util.concurrent.TimeUnit

fun <T> Single<T>.delay(delay: Duration): Single<T> =
	this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T> Flowable<T>.delay(delay: Duration): Flowable<T> =
	this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T> Observable<T>.delay(delay: Duration): Observable<T> =
	this.delay(delay.toMillis(), TimeUnit.MILLISECONDS)

fun <T> Single<T>.timeout(timeout: Duration): Single<T> =
	this.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
