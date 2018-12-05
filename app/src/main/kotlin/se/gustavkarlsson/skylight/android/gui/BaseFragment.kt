package se.gustavkarlsson.skylight.android.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import org.koin.android.ext.android.inject
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.doOnEvery
import se.gustavkarlsson.skylight.android.services.Analytics

abstract class BaseFragment(
	@LayoutRes private val layoutId: Int
) : Fragment() {

	private val navController: NavController by inject()
	private val analytics: Analytics by inject()

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
		getToolbar()?.run {
			if (hasBackStackEntries()) {
				enableBackNavigation()
			}
		}
	}

	override fun onStart() {
		super.onStart()
		analytics.logScreen(requireActivity(), this::class.java.simpleName)
	}

	private fun hasBackStackEntries() = requireFragmentManager().backStackEntryCount > 0

	private fun Toolbar.enableBackNavigation() {
		setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
		setNavigationOnClickListener {
			navController.popBackStack()
		}
	}

	protected open fun getToolbar(): Toolbar? = null

	protected open fun initView() = Unit

	protected open fun bindData(scope: LifecycleScopeProvider<*>) = Unit
}
