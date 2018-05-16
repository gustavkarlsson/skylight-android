package se.gustavkarlsson.skylight.android.gui

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class FluxViewModel<State, Action, Result> : ViewModel() {

	private var statesDisposable: Disposable? = null

	fun postAction(action: Action) = actions.accept(action)

	private val actions = PublishRelay.create<Action>()

	private fun createResults(): Observable<out Result> = actions
		.publish { actions ->
			val results = createActionToResultTransformers()
				.map { actions.compose(it) }
			Observable.merge(results)
		}

	private fun createStates(): Observable<out State> = createResults()
		.scan(createInitialState(), ::accumulateState)
		.replay(1)
		.refCount()
		.observeOn(AndroidSchedulers.mainThread())

	val states: Observable<out State> by lazy {
		createStates().apply {
			statesDisposable = subscribe()
		}
	}

	override fun onCleared() {
		statesDisposable?.dispose()
	}

	protected abstract fun createActionToResultTransformers():
		Iterable<ObservableTransformer<in Action, out Result>>

	protected abstract fun createInitialState(): State

	protected abstract fun accumulateState(last: State, result: Result): State
}
