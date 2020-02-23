package se.gustavkarlsson.skylight.android.feature.main.gui.drawer

import android.content.DialogInterface
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_drawer.*
import se.gustavkarlsson.skylight.android.feature.main.DrawerComponent
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.navigation.navigator
import se.gustavkarlsson.skylight.android.navigation.screens
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.findParentViewByType

internal class DrawerFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_drawer

    private fun closeDrawer() {
        view?.findParentViewByType<NavigationView>()?.let { navView ->
            view?.findParentViewByType<DrawerLayout>()?.closeDrawer(navView)
        }
    }

    private val viewModel by lazy {
        requireParentFragment().getOrRegisterService("drawerViewModel") {
            DrawerComponent.viewModel()
        }
    }

    private val adapter by lazy { DrawerAdapter(viewModel) }

    private var removePlaceDialog: DialogInterface? = null

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        // FIXME this is dumb...
    }

    override fun initView() {
        placesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        placesRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        removePlaceDialog?.dismiss()
        removePlaceDialog = null
        super.onDestroyView()
    }

    override fun bindData() {
        viewModel.drawerItems.bind(this, adapter::setItems)

        viewModel.closeDrawer.bind(this) { closeDrawer() }

        viewModel.navigateToAddPlace.bind(this) {
            navigator.goTo(screens.addPlace())
        }

        viewModel.openRemovePlaceDialog.bind(this) { dialogData ->
                removePlaceDialog?.dismiss()
                val context = requireContext()
                val dialog = MaterialAlertDialogBuilder(context)
                    .setTitle(dialogData.title.resolve(context))
                    .setPositiveButton(R.string.remove) { _, _ ->
                        viewModel.onRemovePlaceClicked(dialogData.placeId)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                removePlaceDialog = dialog
                dialog.show()
            }
    }
}
