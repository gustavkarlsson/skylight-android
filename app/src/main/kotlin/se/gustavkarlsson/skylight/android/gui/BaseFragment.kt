package se.gustavkarlsson.skylight.android.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import kotlinx.android.synthetic.main.activity_main.toolbar
import se.gustavkarlsson.skylight.android.extensions.doOnEvery

abstract class BaseFragment(
	@LayoutRes private val layoutId: Int,
	private val toolbarConfig: ToolbarConfig? = null
) : Fragment() {

	protected val activityToolbar: Toolbar
		get() = (activity as MainActivity).toolbar

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

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setupToolbar(toolbarConfig)
	}

	private fun setupToolbar(toolbarConfig: ToolbarConfig?) {
		if (toolbarConfig != null) {
			activityToolbar.visibility = View.VISIBLE
			activityToolbar.menu.clear()
			toolbarConfig.menu?.let(activityToolbar::inflateMenu)

			activityToolbar.title = ""
			toolbarConfig.title?.let(activityToolbar::setTitle)
		} else {
			activityToolbar.visibility = View.GONE
		}
	}

	protected open fun initView() = Unit

	protected open fun bindData(scope: LifecycleScopeProvider<*>) = Unit

	data class ToolbarConfig(@StringRes val title: Int?, @MenuRes val menu: Int?)
}
