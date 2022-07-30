package se.gustavkarlsson.skylight.android.feature.background.persistence

import android.content.Context
import androidx.core.content.edit
import dagger.Reusable
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.PlaceWithChance
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import javax.inject.Inject

// TODO use datastore
@Reusable
internal class SharedPrefsLastNotificationRepository @Inject constructor(
    context: Context,
) : LastNotificationRepository {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    override fun get(): NotificationRecord? {
        return getTimestamp()?.let { timestamp ->
            val data = getData()
            NotificationRecord(data, timestamp)
        }
    }

    private fun getTimestamp(): Instant? {
        val value = sharedPreferences.getLong(TIMESTAMP_KEY, MISSING_LONG)
            .let { if (it == MISSING_LONG) null else it }
        return value?.let(Instant::ofEpochMilli)
    }

    private fun getData() =
        sharedPreferences.all.mapNotNull { (key, value) ->
            try {
                when (key) {
                    TIMESTAMP_KEY -> null
                    else -> read(key, value)
                }
            } catch (e: Exception) {
                logWarn(e) { "Failed to load notification record key $key from shared prefs" }
                null
            }
        }.toSet()

    private fun read(key: String, value: Any?): PlaceIdWithChance {
        val placeId = getPlaceId(key)
        val chanceLevel = getChanceLevel(value)
        return PlaceIdWithChance(placeId, chanceLevel)
    }

    private fun getPlaceId(key: String): PlaceId {
        val keyId = key.removePrefix(PLACE_ID_KEY_PREFIX)
        return PlaceId.fromLong(keyId.toLong())
    }

    private fun getChanceLevel(value: Any?): ChanceLevel {
        val ordinal = value as Int
        return ChanceLevel.values()[ordinal]
    }

    override fun insert(data: Notification) {
        sharedPreferences.edit {
            clear()
            putLong(TIMESTAMP_KEY, data.timestamp.toEpochMilli())
            data.placesWithChance.forEach { placeWithChance ->
                val key = getKey(placeWithChance)
                val value = getValue(placeWithChance)
                putInt(key, value)
            }
        }
    }

    private fun getKey(placeWithChance: PlaceWithChance): String = PLACE_ID_KEY_PREFIX + placeWithChance.place.id.value

    private fun getValue(placeWithChance: PlaceWithChance): Int = placeWithChance.chanceLevel.ordinal
}

private const val PREFS_FILE_NAME = "last_notification"
private const val TIMESTAMP_KEY = "timestamp"
private const val PLACE_ID_KEY_PREFIX = "place_"
private const val MISSING_LONG = Long.MIN_VALUE
