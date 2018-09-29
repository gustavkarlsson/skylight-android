package se.gustavkarlsson.skylight.android.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import se.gustavkarlsson.skylight.android.extensions.doOnEvery

abstract class BaseFragment(
	@LayoutRes private val layoutId: Int
) : Fragment() {

	init {
		@Suppress("LeakingThis")
		doOnEvery(this, Lifecycle.Event.ON_START) {
			bindData(scope(Lifecycle.Event.ON_STOP))
		}
	}

	final override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(layoutId, container, false)

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		initView()
	}

	protected open fun initView() = Unit

	protected open fun bindData(scope: LifecycleScopeProvider<*>) = Unit
}
