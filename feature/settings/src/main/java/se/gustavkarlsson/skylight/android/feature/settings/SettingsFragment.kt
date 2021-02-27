package se.gustavkarlsson.skylight.android.feature.settings

import android.content.DialogInterface
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ioki.textref.TextRef
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.LegacyScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind

@ExperimentalCoroutinesApi
@FlowPreview
class SettingsFragment : LegacyScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("settingsViewModel") {
            SettingsComponent.build().viewModel()
        }
    }

    private val adapter by lazy { SettingsAdapter(viewModel) }

    override val layoutId: Int = R.layout.fragment_settings

    override val toolbar: MaterialToolbar get() = toolbarView

    private var triggerLevelDialog: DialogInterface? = null

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        toolbarView.fit { Edge.Top }
        itemsRecyclerView.fit { Edge.Bottom }
    }

    override fun initView() {
        itemsRecyclerView.adapter = adapter
    }

    override fun bindView(scope: CoroutineScope) {
        viewModel.settingsItems.bind(scope) { items ->
            adapter.setItems(items)
        }

        viewModel.showSelectTriggerLevel.bind(scope) { (place, triggerLevel) ->
            showTriggerLevelDialog(place, triggerLevel)
        }
    }

    override fun onDestroyView() {
        triggerLevelDialog?.dismiss()
        triggerLevelDialog = null
        itemsRecyclerView.adapter = null
        super.onDestroyView()
    }

    private fun showTriggerLevelDialog(place: Place, currentTriggerLevel: TriggerLevel) {
        triggerLevelDialog?.dismiss()
        val text = TriggerLevel.values()
            .map { it.shortText.resolve(requireContext()) }
            .toTypedArray()
        val checked = TriggerLevel.values().indexOf(currentTriggerLevel)
        val listener = DialogInterface.OnClickListener { dialog, which ->
            val triggerLevel = TriggerLevel.values()[which]
            viewModel.onTriggerLevelSelected(place, triggerLevel)
            dialog.dismiss()
        }
        val dialog = MaterialAlertDialogBuilder(context)
            .setSingleChoiceItems(text, checked, listener)
            .create()
        triggerLevelDialog = dialog
        dialog.show()
    }
}

private val TriggerLevel.shortText: TextRef
    get() = when (this) {
        TriggerLevel.NEVER -> TextRef.stringRes(R.string.pref_notifications_entry_never_short)
        TriggerLevel.LOW -> TextRef.stringRes(R.string.pref_notifications_entry_low_short)
        TriggerLevel.MEDIUM -> TextRef.stringRes(R.string.pref_notifications_entry_medium_short)
        TriggerLevel.HIGH -> TextRef.stringRes(R.string.pref_notifications_entry_high_short)
    }
