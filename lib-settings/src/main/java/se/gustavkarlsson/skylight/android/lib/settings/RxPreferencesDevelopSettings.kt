package se.gustavkarlsson.skylight.android.lib.settings

import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.extensions.seconds

internal class RxPreferencesDevelopSettings(
	context: Context,
	rxSharedPreferences: RxSharedPreferences
) : DevelopSettings {
	private val overrideValuesPreference by lazy {
		val key = context.getString(R.string.pref_override_values_key)
		rxSharedPreferences.getBoolean(key, false)
	}

	private val kpIndexPreference by lazy {
		val key = context.getString(R.string.pref_kp_index_key)
		rxSharedPreferences.getInteger(key, 5)
	}

	private val geomagLatitudePreference by lazy {
		val key = context.getString(R.string.pref_geomag_latitude_key)
		rxSharedPreferences.getInteger(key, 0)
	}

	private val sunZenithAnglePreference by lazy {
		val key = context.getString(R.string.pref_sun_position_key)
		rxSharedPreferences.getInteger(key, 90)
	}

	private val cloudPercentagePreference by lazy {
		val key = context.getString(R.string.pref_cloud_percentage_key)
		rxSharedPreferences.getInteger(key, 50)
	}

	private val refreshDurationSecondsPreference by lazy {
		val key = context.getString(R.string.pref_refresh_duration_seconds_key)
		rxSharedPreferences.getInteger(key, 2)
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
		get() = cloudPercentagePreference.get()

	override val refreshDuration: Duration
		get() = refreshDurationSecondsPreference.get().seconds

}
