package se.gustavkarlsson.skylight.android.feature.settings

import android.content.DialogInterface
import androidx.appcompat.widget.Toolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ioki.textref.TextRef
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind

internal class SettingsFragment : BaseFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val adapter by lazy { SettingsAdapter(viewModel) }

    override val layoutId: Int = R.layout.fragment_settings

    override val toolbar: Toolbar? get() = toolbarView

    private var triggerLevelDialog: DialogInterface? = null

    override fun initView() {
        itemsRecyclerView.adapter = adapter
    }

    override fun bindData() {
        viewModel.settingsItems.bind(this, adapter::setItems)

        viewModel.showSelectTriggerLevel.bind(this, ::showTriggerLevelDialog)
    }

    override fun onDestroyView() {
        triggerLevelDialog?.dismiss()
        triggerLevelDialog = null
        super.onDestroyView()
    }

    private fun showTriggerLevelDialog(place: Place) {
        triggerLevelDialog?.dismiss()
        val text = TriggerLevel.values()
            .map { it.shortText.resolve(requireContext()) }
            .toTypedArray()
        val listener = DialogInterface.OnClickListener { _, which ->
            val triggerLevel = TriggerLevel.values()[which]
            viewModel.onTriggerLevelSelected(place, triggerLevel)
        }
        val dialog = MaterialAlertDialogBuilder(context)
            .setItems(text, listener)
            .create()
        triggerLevelDialog = dialog
        dialog.show()
    }
}

private val TriggerLevel.shortText: TextRef
    get() = when (this) {
        TriggerLevel.NEVER -> TextRef(R.string.pref_notifications_entry_never_short)
        TriggerLevel.LOW -> TextRef(R.string.pref_notifications_entry_low_short)
        TriggerLevel.MEDIUM -> TextRef(R.string.pref_notifications_entry_medium_short)
        TriggerLevel.HIGH -> TextRef(R.string.pref_notifications_entry_high_short)
    }
