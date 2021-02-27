package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus

abstract class LegacyUiFragment : Fragment() {

    private var liveScope: CoroutineScope? = null

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
    override fun onStart() {
        super.onStart()
        val liveScope = MainScope() + CoroutineName("liveScope")
        this.liveScope = liveScope
        bindView(liveScope)
    }

    @CallSuper
    override fun onStop() {
        liveScope?.cancel()
        liveScope = null
        super.onStop()
    }

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected abstract fun initView()

    protected abstract fun bindView(scope: CoroutineScope)
}
