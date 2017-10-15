package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.SharedPreferences
import dagger.Reusable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel
import javax.inject.Inject

@Reusable
class SharedPreferencesSettings
@Inject
constructor(
	private val sharedPreferences: SharedPreferences,
	context: Context
) : Settings {
	private val notificationsKey: String = context.getString(R.string.pref_notifications_key)
	private val notificationsDefaultValue: Boolean = context.resources.getBoolean(R.bool.pref_notifications_default)
	private val triggerLevelKey: String = context.getString(R.string.pref_trigger_level_key)
	private val triggerLevelDefaultValue: String = context.resources.getString(R.string.pref_trigger_level_default)

	override val isEnableNotifications: Boolean
		get() = sharedPreferences.getBoolean(notificationsKey, notificationsDefaultValue)

	override val triggerLevel: ChanceLevel
		get() {
			val triggerLevelRaw = sharedPreferences.getString(triggerLevelKey, triggerLevelDefaultValue)
			val triggerLevel = Integer.parseInt(triggerLevelRaw)
			return ChanceLevel.values()[triggerLevel]
		}

}
