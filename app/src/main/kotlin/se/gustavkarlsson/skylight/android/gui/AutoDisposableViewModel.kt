package se.gustavkarlsson.skylight.android.gui

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.autodispose.LifecycleEndedException
import com.uber.autodispose.LifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.functions.Function
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel.ViewModelState.ACTIVE
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel.ViewModelState.CLEARED

abstract class AutoDisposableViewModel : ViewModel() {

    private val scopeProvider = ViewModelLifecycleScopeProvider()

    fun scope(): LifecycleScopeProvider<ViewModelState> = scopeProvider

    override fun onCleared() {
        scopeProvider.clear()
    }

    enum class ViewModelState {
        ACTIVE, CLEARED
    }

    private class ViewModelLifecycleScopeProvider : LifecycleScopeProvider<ViewModelState> {

        private val lifecycle = BehaviorRelay.createDefault<ViewModelState>(
			ACTIVE
		)

        override fun peekLifecycle(): ViewModelState? = lifecycle.value

        override fun lifecycle(): Observable<ViewModelState> = lifecycle

        override fun correspondingEvents() =
			correspondingEvents

        fun clear() {
            lifecycle.accept(CLEARED)
        }

        companion object {
            private val correspondingEvents = Function<ViewModelState, ViewModelState> {
                when (it) {
                    ACTIVE -> CLEARED
                    else -> throw LifecycleEndedException("Lifecycle has ended!")
                }
            }
        }
    }
}
