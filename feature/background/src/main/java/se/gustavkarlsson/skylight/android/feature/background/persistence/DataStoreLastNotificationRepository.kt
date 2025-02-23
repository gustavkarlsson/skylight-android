package se.gustavkarlsson.skylight.android.feature.background.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification
import se.gustavkarlsson.skylight.android.feature.background.notifications.PlaceWithChance
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import java.io.IOException

@Inject
internal class DataStoreLastNotificationRepository(
    context: Context,
) : LastNotificationRepository {

    private val dataStore = context.dataStore

    override suspend fun get(): NotificationRecord? {
        return try {
            val prefs = dataStore.data.first()
            val timestamp = prefs.getTimestamp() ?: return null
            val data = prefs.getData()
            NotificationRecord(data, timestamp)
        } catch (e: IOException) {
            logWarn(e) { "Failed to get notification record" }
            null
        }
    }

    private fun Preferences.getTimestamp(): Instant? {
        val timestamp = get(TIMESTAMP_KEY) ?: return null
        return Instant.fromEpochMilliseconds(timestamp)
    }

    private fun Preferences.getData(): Set<PlaceIdWithChance> {
        return this.asMap()
            .mapNotNull { (key, value) ->
                try {
                    when (key) {
                        TIMESTAMP_KEY -> null
                        else -> read(key, value)
                    }
                } catch (e: Exception) {
                    logWarn(e) { "Failed to load notification record key '$key' from shared prefs" }
                    null
                }
            }
            .toSet()
    }

    private fun read(key: Preferences.Key<*>, value: Any?): PlaceIdWithChance {
        val placeId = getPlaceId(key.name)
        val chanceLevel = getChanceLevel(value)
        return PlaceIdWithChance(placeId, chanceLevel)
    }

    private fun getPlaceId(key: String): PlaceId {
        val keyId = key.removePrefix(PLACE_ID_KEY_PREFIX)
        return PlaceId.fromLong(keyId.toLong())
    }

    private fun getChanceLevel(value: Any?): ChanceLevel {
        val ordinal = value as Int
        return ChanceLevel.entries[ordinal]
    }

    override suspend fun insert(notification: Notification) {
        val timestampValue = notification.timestamp.toEpochMilliseconds()
        val dataPairs = notification.placesWithChance.map { placeWithChance ->
            val key = placeWithChance.getKey()
            val value = placeWithChance.getValue()
            key to value
        }
        try {
            dataStore.edit { prefs ->
                prefs.clear()
                prefs[TIMESTAMP_KEY] = timestampValue
                prefs.putAll(*dataPairs.toTypedArray())
            }
        } catch (e: IOException) {
            logWarn(e) { "Failed to insert $notification" }
        }
    }

    private fun PlaceWithChance.getKey(): Preferences.Key<Int> {
        val keyString = PLACE_ID_KEY_PREFIX + place.id.value
        return intPreferencesKey(keyString)
    }

    private fun PlaceWithChance.getValue(): Int = chanceLevel.ordinal
}

private const val PREFS_FILE_NAME = "last_notification"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFS_FILE_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, PREFS_FILE_NAME))
    },
)

private val TIMESTAMP_KEY = longPreferencesKey("timestamp")
private const val PLACE_ID_KEY_PREFIX = "place_"
