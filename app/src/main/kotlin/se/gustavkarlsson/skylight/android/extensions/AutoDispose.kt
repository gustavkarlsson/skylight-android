package se.gustavkarlsson.skylight.android.extensions

import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Maybe
import io.reactivex.Single

fun <T> T.doWhen(
	provider: LifecycleScopeProvider<*>,
	dispose: (T) -> Unit
): T {
	Single.never<Unit>()
		.doOnDispose { dispose(this) }
		.autoDisposable(provider)
		.subscribe()
	return this
}

fun <T> T.doWhen(
	scope: Maybe<*>,
	dispose: (T) -> Unit
): T {
	Single.never<Unit>()
		.doOnDispose { dispose(this) }
		.autoDisposable(scope)
		.subscribe()
	return this
}

fun <T> T.doWhen(
	provider: ScopeProvider,
	dispose: (T) -> Unit
): T {
	Single.never<Unit>()
		.doOnDispose { dispose(this) }
		.autoDisposable(provider)
		.subscribe()
	return this
}
