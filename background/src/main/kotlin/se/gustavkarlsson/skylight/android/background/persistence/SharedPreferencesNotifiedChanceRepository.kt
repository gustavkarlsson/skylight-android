package se.gustavkarlsson.skylight.android.background.persistence

import android.content.Context
import androidx.core.content.edit
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.NotifiedChance

class SharedPreferencesNotifiedChanceRepository(
	context: Context
) : NotifiedChanceRepository {

	private val sharedPreferences =
		context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

	override fun get(): NotifiedChance? {
		return getTimestamp()?.let { timestamp ->
			val chance = getChance()
			NotifiedChance(chance, timestamp)
		}
	}

	private fun getChance(): Chance {
		val value = sharedPreferences.getFloat(KEY_CHANCE, MISSING_FLOAT)
			.let { if (it == MISSING_FLOAT) null else it.toDouble() }
		return Chance(value)
	}

	private fun getTimestamp(): Instant? {
		val value = sharedPreferences.getLong(KEY_TIMESTAMP, MISSING_LONG)
			.let { if (it == MISSING_LONG) null else it }
		return value?.let(Instant::ofEpochMilli)
	}

	override fun insert(notifiedChance: NotifiedChance) {
		sharedPreferences.edit {
			putFloat(KEY_CHANCE, notifiedChance.chance.value?.toFloat() ?: MISSING_FLOAT)
			putLong(KEY_TIMESTAMP, notifiedChance.timestamp.toEpochMilli())
		}
	}

	companion object {
		private const val PREFS_FILE_NAME = "notified_chance"
		private const val KEY_CHANCE = "chance"
		private const val KEY_TIMESTAMP = "timestamp"
		private val MISSING_FLOAT = Float.NaN
		private const val MISSING_LONG = Long.MIN_VALUE
	}

}
