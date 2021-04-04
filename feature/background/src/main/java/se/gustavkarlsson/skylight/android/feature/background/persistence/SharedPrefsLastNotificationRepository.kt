package se.gustavkarlsson.skylight.android.feature.background.persistence

import android.content.Context
import androidx.core.content.edit
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.PlaceWithChance

internal class SharedPrefsLastNotificationRepository(
    context: Context
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
                    CURRENT_KEY -> readAsCurrent(value)
                    else -> readAsCustom(key, value)
                }
            } catch (e: Exception) {
                logWarn(e) { "Failed to load notification record key $key from shared prefs" }
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
            val placeRef = PlaceRef.Saved(placeId)
            val chanceLevel = ChanceLevel.values()[intValue]
            PlaceRefWithChance(
                placeRef,
                chanceLevel
            )
        }

    override fun insert(data: Notification) {
        sharedPreferences.edit {
            clear()
            putLong(TIMESTAMP_KEY, data.timestamp.toEpochMilli())
            data.data.forEach { placeWithChance ->
                val key = getKey(placeWithChance)
                val value = getValue(placeWithChance)
                putInt(key, value)
            }
        }
    }

    private fun getKey(value: PlaceWithChance) = value.place.id?.toString() ?: CURRENT_KEY

    private fun getValue(value: PlaceWithChance) = value.chanceLevel.ordinal
}

private const val PREFS_FILE_NAME = "last_notification"
private const val TIMESTAMP_KEY = "timestamp"
private const val CURRENT_KEY = "current"
private const val MISSING_LONG = Long.MIN_VALUE
