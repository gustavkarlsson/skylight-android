package se.gustavkarlsson.skylight.android.feature.background.persistence

import android.content.Context
import androidx.core.content.edit
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.PlaceWithChance
import timber.log.Timber

internal class SharedPrefsLastNotificationRepository(
	context: Context
) : LastNotificationRepository {

	private val sharedPreferences =
		context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

	override fun get(): NotificationRecord? {
		return getTimestamp()?.let { timestamp ->
			val data = getData()
			NotificationRecord(data, timestamp)
		}
	}

	private fun getTimestamp(): Instant? {
		val value = sharedPreferences.getLong(KEY_TIMESTAMP, MISSING_LONG)
			.let { if (it == MISSING_LONG) null else it }
		return value?.let(Instant::ofEpochMilli)
	}

	private fun getData() =
		sharedPreferences.all.mapNotNull { (key, value) ->
			try {
				when (key) {
					KEY_TIMESTAMP -> null
					KEY_CURRENT -> readAsCurrent(value)
					else -> readAsCustom(key, value)
				}
			} catch (e: Exception) {
				Timber.w(e, "Failed to load notification record key %s from shared prefs", key)
				null
			}
		}.toSet()

	private fun readAsCurrent(value: Any?) =
		(value as? Int)?.let { intValue ->
			val placeRef = PlaceRef.Current
			val chanceLevel = ChanceLevel.values()[intValue]
			PlaceRefWithChance(
				placeRef,
				chanceLevel
			)
		}


	private fun readAsCustom(key: String, value: Any?) =
		(value as? Int)?.let { intValue ->
			val placeId = key.toLong()
			val placeRef = PlaceRef.Custom(placeId)
			val chanceLevel = ChanceLevel.values()[intValue]
			PlaceRefWithChance(
				placeRef,
				chanceLevel
			)
		}


	override fun insert(data: Notification) {
		sharedPreferences.edit {
			clear()
			putLong(KEY_TIMESTAMP, data.timestamp.toEpochMilli())
			data.data.forEach { placeWithChance ->
				val key = getKey(placeWithChance)
				val value = getValue(placeWithChance)
				putInt(key, value)
			}
		}
	}

	private fun getKey(value: PlaceWithChance) =
		when (val place = value.place) {
			Place.Current -> KEY_CURRENT
			is Place.Custom -> place.id.toString()
		}

	private fun getValue(value: PlaceWithChance) = value.chanceLevel.ordinal
}

private const val PREFS_FILE_NAME = "last_notification"
private const val KEY_TIMESTAMP = "timestamp"
private const val KEY_CURRENT = "current"
private const val MISSING_LONG = Long.MIN_VALUE
