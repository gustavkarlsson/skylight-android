package se.gustavkarlsson.skylight.android.gui

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class FluxViewModel<Event, State, Action, Result> : ViewModel() {

	private var statesDisposable: Disposable? = null

	private val events = PublishRelay.create<Event>()

	private fun createActions(): Observable<out Action> = events
		.publish { events ->
			val actions = createEventToActionTransformers()
				.map { events.compose(it) }
			Observable.merge(actions)
		}

	private fun createResults(): Observable<out Result> = createActions()
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

	fun postEvent(event: Event) = events.accept(event)

	val states: Observable<out State> by lazy {
		createStates().apply {
			statesDisposable = subscribe()
		}
	}

	override fun onCleared() {
		statesDisposable?.dispose()
	}

	protected abstract fun createEventToActionTransformers(): Iterable<ObservableTransformer<in Event, out Action>>

	protected abstract fun createActionToResultTransformers(): Iterable<ObservableTransformer<in Action, out Result>>

	protected abstract fun createInitialState(): State

	protected abstract fun accumulateState(last: State, result: Result): State
}
