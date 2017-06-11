package se.gustavkarlsson.skylight.android.cache

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.extensions.fromJson
import se.gustavkarlsson.skylight.android.models.AuroraReport


private val instantTypeAdapter = object : TypeAdapter<Instant>() {
	override fun read(reader: JsonReader): Instant {
		return Instant.ofEpochMilli(reader.nextLong())
	}

	override fun write(writer: JsonWriter, instant: Instant) {
		writer.value(instant.toEpochMilli())
	}
}


private val auroraReportGson = GsonBuilder()
		.registerTypeAdapter(Instant::class.java, instantTypeAdapter)
		.create()!!

val auroraReportCacheSerializer = object : CacheSerializer<AuroraReport> {
	private val gson = auroraReportGson

	override fun fromString(json: String): AuroraReport {
		return gson.fromJson(json)
	}

	override fun toString(report: AuroraReport): String {
		return gson.toJson(report)
	}
}
