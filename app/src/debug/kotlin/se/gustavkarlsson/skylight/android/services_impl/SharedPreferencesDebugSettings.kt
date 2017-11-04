package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.content.SharedPreferences
import dagger.Reusable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.DebugSettings
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import javax.inject.Inject

@Reusable
class SharedPreferencesDebugSettings
@Inject
constructor(
	private val sharedPreferences: SharedPreferences,
	context: Context
) : DebugSettings {
	private val overrideValuesKey: String = context.getString(R.string.pref_override_values_key)
	private val kpIndexKey: String = context.getString(R.string.pref_kp_index_key)
	private val geomagLatitudeKey: String = context.getString(R.string.pref_geomag_latitude_key)
	private val sunZenithAngleKey: String = context.getString(R.string.pref_sun_position_key)
	private val cloudPercentageKey: String = context.getString(R.string.pref_cloud_percentage_key)

	override val overrideValues: Boolean
        get() = sharedPreferences.getBoolean(overrideValuesKey, false)

    override val kpIndex: Double
        get() = parseDouble(sharedPreferences.getString(kpIndexKey, "0"))

    override val geomagLatitude: Double
        get() = parseDouble(sharedPreferences.getString(geomagLatitudeKey, "0"))

    override val sunZenithAngle: Double
        get() = parseDouble(sharedPreferences.getString(sunZenithAngleKey, "0"))

    override val cloudPercentage: Int
        get() = parseInt(sharedPreferences.getString(cloudPercentageKey, "0"))

}
