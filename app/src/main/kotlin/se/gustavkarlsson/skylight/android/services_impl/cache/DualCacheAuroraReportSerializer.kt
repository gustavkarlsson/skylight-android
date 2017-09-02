package se.gustavkarlsson.skylight.android.services_impl.cache

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.fromJson


private val instantTypeAdapter = object : TypeAdapter<Instant>() {
	override fun read(reader: JsonReader) = Instant.ofEpochMilli(reader.nextLong())

	override fun write(writer: JsonWriter, instant: Instant) {
		writer.value(instant.toEpochMilli())
	}
}

val auroraReportCacheSerializer = object : CacheSerializer<AuroraReport> {
	private val gson = GsonBuilder()
			.registerTypeAdapter(Instant::class.java, instantTypeAdapter)
			.create()

	override fun fromString(json: String): AuroraReport = gson.fromJson(json)

	override fun toString(report: AuroraReport): String = gson.toJson(report)
}
