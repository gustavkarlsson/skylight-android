package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import kotlinx.coroutines.CoroutineScope

abstract class LegacyUiFragment : ScopeFragment() {

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId, container, false)

    @CallSuper
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        initView()
    }

    @CallSuper
    override fun onNewStartStopScope(scope: CoroutineScope) {
        bindView(scope)
    }

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected abstract fun initView()

    protected abstract fun bindView(scope: CoroutineScope)
}
