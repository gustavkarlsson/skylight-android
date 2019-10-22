package se.gustavkarlsson.skylight.android.feature.settings

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.view.ContextThemeWrapper
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.ext.android.get
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.lib.settings.Settings

internal class InnerSettingsFragment : PreferenceFragmentCompat() {

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		createPreferences(getThemedContext())
		// FIXME conditionally add develop settings
	}

	private fun getThemedContext(): ContextThemeWrapper {
		val activityContext = requireActivity()
		val themeTypedValue = TypedValue().also {
			activityContext.theme.resolveAttribute(R.attr.preferenceTheme, it, true)
		}
		return ContextThemeWrapper(activityContext, themeTypedValue.resourceId)
	}

	private fun createPreferences(themedContext: ContextThemeWrapper) {
		val preferenceScreen = preferenceManager.createPreferenceScreen(themedContext)
		setPreferenceScreen(preferenceScreen)

		// TODO category looks pretty bad
		val notificationsCategory = PreferenceCategory(themedContext).apply {
			title = getString(R.string.pref_notifications_category_title)
			summary = getString(R.string.pref_notifications_category_summary)
		}.also { preferenceScreen.addPreference(it) }

		val defaultValue = ChanceLevel.values().indexOf(Settings.DEFAULT_TRIGGER_LEVEL)
		val entriesArray = resources.getStringArray(R.array.pref_notifications_level_entries)
		val entryValuesArray = resources.getStringArray(R.array.pref_notifications_level_values)

		get<Settings>().notificationTriggerLevels.blockingFirst()
			.map { (place, _) ->
				ListPreference(themedContext).apply {
					key = place.name.resolve(themedContext)
					title = place.name.resolve(themedContext)
					summary = "%s"
					entries = entriesArray
					entryValues = entryValuesArray
					setDefaultValue(defaultValue)
				}
			}
			.forEach { notificationsCategory.addPreference(it) }
	}
}
