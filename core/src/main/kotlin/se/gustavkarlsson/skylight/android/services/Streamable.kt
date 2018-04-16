package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable

interface Streamable<T> {
	val stream: Flowable<T>
}
