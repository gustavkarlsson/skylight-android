package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.DebugSettings

class RxPreferencesDebugSettings(
	context: Context,
	rxSharedPreferences: RxSharedPreferences
) : DebugSettings {
	private val overrideValuesPreference by lazy {
		val key = context.getString(R.string.pref_override_values_key)
		val preference = rxSharedPreferences.getBoolean(key, false)
		preference
	}

	private val kpIndexPreference by lazy {
		val key = context.getString(R.string.pref_kp_index_key)
		val preference = rxSharedPreferences.getString(key, "0")
		preference
	}

	private val geomagLatitudePreference by lazy {
		val key = context.getString(R.string.pref_geomag_latitude_key)
		val preference = rxSharedPreferences.getString(key, "0")
		preference
	}

	private val sunZenithAnglePreference by lazy {
		val key = context.getString(R.string.pref_sun_position_key)
		val preference = rxSharedPreferences.getString(key, "0")
		preference
	}

	private val cloudPercentagePreference by lazy {
		val key = context.getString(R.string.pref_cloud_percentage_key)
		val preference = rxSharedPreferences.getString(key, "0")
		preference
	}

	override val overrideValues: Boolean
        get() = overrideValuesPreference.get()

	override val overrideValuesChanges: Flowable<Boolean>
		get() = overrideValuesPreference.asObservable()
			.toFlowable(BackpressureStrategy.LATEST)

    override val kpIndex: Double
        get() = kpIndexPreference.get().toDouble()

    override val geomagLatitude: Double
        get() = geomagLatitudePreference.get().toDouble()

    override val sunZenithAngle: Double
        get() = sunZenithAnglePreference.get().toDouble()

    override val cloudPercentage: Int
        get() = cloudPercentagePreference.get().toInt()

}
