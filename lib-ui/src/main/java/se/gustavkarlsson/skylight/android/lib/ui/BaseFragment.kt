package se.gustavkarlsson.skylight.android.lib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.koin.android.ext.android.inject
import se.gustavkarlsson.skylight.android.services.Analytics

abstract class BaseFragment : Fragment() {

	private val analytics: Analytics by inject()

	private val navigator: Navigator by inject()

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
		toolbar?.let(::setupBackNavigation)
	}

	private fun setupBackNavigation(toolbar: Toolbar) {
		if (requireFragmentManager().backStackEntryCount > 0) {
			toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
			toolbar.setNavigationOnClickListener { navigator.goBack() }
		}
	}

	override fun onStart() {
		super.onStart()
		analytics.logScreen(requireActivity(), this::class.java.simpleName)
	}

	@get:LayoutRes
	protected abstract val layoutId: Int

	protected open val toolbar: Toolbar? = null

	protected open fun initView() = Unit

	protected open fun bindData(scope: LifecycleScopeProvider<*>) = Unit
}
