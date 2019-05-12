package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_main_drawer.placesRecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.features.base.BaseFragment
import se.gustavkarlsson.skylight.android.features.base.findParentViewByType

class DrawerFragment : BaseFragment(R.layout.fragment_main_drawer) {

	private val adapter = PlacesAdapter {
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
			.subscribe {
				adapter.items = it
			}
	}
}
