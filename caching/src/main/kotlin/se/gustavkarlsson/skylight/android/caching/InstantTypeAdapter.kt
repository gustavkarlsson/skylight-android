package se.gustavkarlsson.skylight.android.caching

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.threeten.bp.Instant

internal class InstantTypeAdapter : TypeAdapter<Instant>() {
	override fun read(reader: JsonReader): Instant = Instant.ofEpochMilli(reader.nextLong())

	override fun write(writer: JsonWriter, instant: Instant) {
		writer.value(instant.toEpochMilli())
	}
}
