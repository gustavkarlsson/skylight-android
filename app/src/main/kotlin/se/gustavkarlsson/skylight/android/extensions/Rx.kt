package se.gustavkarlsson.skylight.android.extensions

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import org.reactivestreams.Publisher
import se.gustavkarlsson.skylight.android.services.SingletonCache


fun <T> Flowable<T>.singletonCache(cache: SingletonCache<T>): Flowable<T> {
	return this.compose(SingletonCachingTransformer(cache))
}

private class SingletonCachingTransformer<T>(private val cache: SingletonCache<T>) :
	FlowableTransformer<T, T> {
	override fun apply(upstream: Flowable<T>): Publisher<T> {
		return Flowable.concat(Flowable.fromCallable { cache.value }, upstream)
			.doOnNext { cache.value = it }
	}
}
