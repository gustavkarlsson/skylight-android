package se.gustavkarlsson.skylight.android.feature.main.drawer

import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_drawer.placesRecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.findParentViewByType

internal class DrawerFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_drawer

	private val adapter = DrawerAdapter()

	private fun closeDrawer() {
		view?.findParentViewByType<NavigationView>()?.let { navView ->
			view?.findParentViewByType<DrawerLayout>()?.closeDrawer(navView)
		}
	}

	private val viewModel by viewModel<DrawerViewModel>()

	override fun initView() {
		placesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		placesRecyclerView.adapter = adapter
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		viewModel.places
			.autoDisposable(scope)
			.subscribe(adapter::setItems)

		viewModel.closeDrawer
			.autoDisposable(scope)
			.subscribe { closeDrawer() }

		viewModel.openRemoveLocationDialog
			.autoDisposable(scope)
			.subscribe {
				val context = requireContext()
				MaterialAlertDialogBuilder(context)
					.setTitle(it.title.resolve(context))
					.setPositiveButton(R.string.remove) { _, _ -> it.onConfirm() }
					.setNegativeButton(R.string.cancel, null)
					.show()
			}
	}
}
