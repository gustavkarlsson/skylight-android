package se.gustavkarlsson.skylight.android.ui


import com.jakewharton.rxrelay2.PublishRelay
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.observers.TestObserver
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel.ViewModelState
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel.ViewModelState.ACTIVE
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel.ViewModelState.CLEARED

class AutoDisposableViewModelTest {

    private lateinit var impl: TestViewModel

    @Before
    fun setUp() {
        impl = TestViewModel()
    }

    @Test
    fun scopePeekLifecycle_initial_isActive() {
        val lifecycleState = impl.scope().peekLifecycle()
        lifecycleState shouldEqual ACTIVE
    }

    @Test
    fun scopePeekLifecycle_afterClear_isCleared() {
        impl.clear()

        val lifecycleState = impl.scope().peekLifecycle()
        lifecycleState shouldEqual CLEARED
    }


    @Test
    fun scopeLifecycle_initial_isActive() {
        val testObserver = TestObserver<ViewModelState>()
        impl.scope().lifecycle()
                .subscribe(testObserver)

        testObserver.assertValue(ACTIVE)
    }


    @Test
    fun scopeLifecycle_afterClear_isCleared() {
        val testObserver = TestObserver<ViewModelState>()
        impl.scope().lifecycle()
                .subscribe(testObserver)

        impl.clear()

        testObserver.assertValues(ACTIVE, CLEARED)
    }


    @Test
    fun autoDisposableSubscribe_afterClear_doesNotReceiveNewValues() {
        val testObserver = TestObserver<Int>()
        val relay = PublishRelay.create<Int>()
        relay
                .autoDisposable(impl.scope())
                .subscribe(testObserver)

        impl.clear()
        relay.accept(1)

        testObserver.assertNoValues()
    }

    private class TestViewModel : AutoDisposableViewModel() {
        fun clear() = onCleared()
    }
}
