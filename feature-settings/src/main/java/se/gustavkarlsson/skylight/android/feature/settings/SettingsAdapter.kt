package se.gustavkarlsson.skylight.android.feature.settings

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ioki.textref.TextRef
import kotlinx.android.synthetic.main.layout_settings_item.view.*
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.TriggerLevel

internal class SettingsAdapter(
	private val viewModel: SettingsViewModel
) : RecyclerView.Adapter<ViewHolder>() {

	private var items: List<Pair<Place, TriggerLevel>> = emptyList()

	fun setItems(newItems: List<Pair<Place, TriggerLevel>>) {
		items = newItems
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.layout_settings_item, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount() = items.size

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val (place, triggerLevel) = items[position]
		val view = holder.itemView
		view.title.text = place.name.resolve(view.context)
		view.subtitle.text = triggerLevel.longText.resolve(view.context)
		view.setOnClickListener {
			showTriggerLevelDialog(view.context, place)
		}
	}

	private fun showTriggerLevelDialog(context: Context, place: Place) {
		val text = TriggerLevel.values()
			.map { it.shortText.resolve(context) }
			.toTypedArray()
		val listener = DialogInterface.OnClickListener { _, which ->
			val triggerLevel = TriggerLevel.values()[which]
			viewModel.onTriggerLevelSelected(place, triggerLevel)
		}
		AlertDialog.Builder(context)
			.setItems(text, listener)
			.show()
	}

}

internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

private val TriggerLevel.shortText: TextRef
	get() = when (this) {
		TriggerLevel.NEVER -> TextRef(R.string.pref_notifications_entry_never_short)
		TriggerLevel.LOW -> TextRef(R.string.pref_notifications_entry_low_short)
		TriggerLevel.MEDIUM -> TextRef(R.string.pref_notifications_entry_medium_short)
		TriggerLevel.HIGH -> TextRef(R.string.pref_notifications_entry_high_short)
	}

private val TriggerLevel.longText: TextRef
	get() = when (this) {
		TriggerLevel.NEVER -> TextRef(R.string.pref_notifications_entry_never_long)
		TriggerLevel.LOW -> TextRef(R.string.pref_notifications_entry_low_long)
		TriggerLevel.MEDIUM -> TextRef(R.string.pref_notifications_entry_medium_long)
		TriggerLevel.HIGH -> TextRef(R.string.pref_notifications_entry_high_long)
	}
