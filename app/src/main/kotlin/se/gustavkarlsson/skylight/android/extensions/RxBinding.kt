package se.gustavkarlsson.skylight.android.extensions

import android.arch.lifecycle.LifecycleOwner
import com.uber.autodispose.*
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers


fun <T> Flowable<T>.forUi(lifecycleOwner: LifecycleOwner): FlowableSubscribeProxy<T> {
	return this
		.observeOn(AndroidSchedulers.mainThread())
		.autoDisposable(lifecycleOwner.scope())
}

fun <T> Observable<T>.forUi(lifecycleOwner: LifecycleOwner): ObservableSubscribeProxy<T> {
	return this
		.observeOn(AndroidSchedulers.mainThread())
		.autoDisposable(lifecycleOwner.scope())
}

fun <T> Single<T>.forUi(lifecycleOwner: LifecycleOwner): SingleSubscribeProxy<T> {
	return this
		.observeOn(AndroidSchedulers.mainThread())
		.autoDisposable(lifecycleOwner.scope())
}

fun <T> Maybe<T>.forUi(lifecycleOwner: LifecycleOwner): MaybeSubscribeProxy<T> {
	return this
		.observeOn(AndroidSchedulers.mainThread())
		.autoDisposable(lifecycleOwner.scope())
}

fun Completable.forUi(lifecycleOwner: LifecycleOwner): CompletableSubscribeProxy {
	return this
		.observeOn(AndroidSchedulers.mainThread())
		.autoDisposable(lifecycleOwner.scope())
}
