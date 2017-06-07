package se.gustavkarlsson.skylight.android.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Reusable
import se.gustavkarlsson.skylight.android.R
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import javax.inject.Inject

@Reusable
class SharedPreferencesDebugSettings
@Inject
constructor(context: Context) : DebugSettings {
    private val defaultPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
	private val overrideValuesKey: String = context.getString(R.string.pref_override_values_key)
	private val kpIndexKey: String = context.getString(R.string.pref_kp_index_key)
	private val geomagLatitudeKey: String = context.getString(R.string.pref_geomag_latitude_key)
	private val sunZenithAngleKey: String = context.getString(R.string.pref_sun_position_key)
	private val cloudPercentageKey: String = context.getString(R.string.pref_cloud_percentage_key)

	override val isOverrideValues: Boolean
        get() = defaultPreferences.getBoolean(overrideValuesKey, false)

    override val kpIndex: Float
        get() = parseFloat(defaultPreferences.getString(kpIndexKey, "0"))

    override val geomagLatitude: Float
        get() = parseFloat(defaultPreferences.getString(geomagLatitudeKey, "0"))

    override val sunZenithAngle: Float
        get() = parseFloat(defaultPreferences.getString(sunZenithAngleKey, "0"))

    override val cloudPercentage: Int
        get() = parseInt(defaultPreferences.getString(cloudPercentageKey, "0"))

}
