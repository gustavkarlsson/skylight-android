package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*

abstract class BaseFragment : Fragment() {

    private var viewScope: CoroutineScope? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId, container, false)

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val viewScope = MainScope() + CoroutineName("viewScope")
        this.viewScope = viewScope
        initView(viewScope)
        bindData(viewScope)
    }

    override fun onDestroyView() {
        viewScope?.cancel()
        viewScope = null
        super.onDestroyView()
    }

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected open fun initView(viewScope: CoroutineScope) = Unit

    protected open fun bindData(viewScope: CoroutineScope) = Unit
}
