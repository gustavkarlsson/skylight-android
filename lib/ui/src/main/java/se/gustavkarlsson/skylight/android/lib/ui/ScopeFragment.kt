package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus

abstract class ScopeFragment : Fragment() {

    // Create Destroy
    protected var createDestroyScope: CoroutineScope? = null
        private set

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scope = MainScope() + CoroutineName("createDestroyScope")
        createDestroyScope = scope
        onNewCreateDestroyScope(scope)
    }

    protected open fun onNewCreateDestroyScope(scope: CoroutineScope) {}

    @CallSuper
    override fun onDestroy() {
        createDestroyScope?.cancel("onDestroy called")
        createDestroyScope = null
        super.onDestroy()
    }

    // Start Stop
    protected var startStopScope: CoroutineScope? = null
        private set

    @CallSuper
    override fun onStart() {
        super.onStart()
        val scope = MainScope() + CoroutineName("startStopScope")
        startStopScope = scope
        onNewStartStopScope(scope)
    }

    protected open fun onNewStartStopScope(scope: CoroutineScope) {}

    @CallSuper
    override fun onStop() {
        startStopScope?.cancel("onStop called")
        startStopScope = null
        super.onStop()
    }

    // Resume Pause
    protected var resumePauseScope: CoroutineScope? = null
        private set

    @CallSuper
    override fun onResume() {
        super.onResume()
        val scope = MainScope() + CoroutineName("resumePauseScope")
        resumePauseScope = scope
        onNewResumePauseScope(scope)
    }

    protected open fun onNewResumePauseScope(scope: CoroutineScope) {}

    @CallSuper
    override fun onPause() {
        resumePauseScope?.cancel("onPause called")
        resumePauseScope = null
        super.onPause()
    }

    // View
    protected var viewScope: CoroutineScope? = null
        private set

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scope = MainScope() + CoroutineName("viewScope")
        viewScope = scope
        onNewViewScope(scope)
    }

    protected open fun onNewViewScope(scope: CoroutineScope) {}

    @CallSuper
    override fun onDestroyView() {
        viewScope?.cancel("onDestroyView called")
        viewScope = null
        super.onDestroyView()
    }
}
