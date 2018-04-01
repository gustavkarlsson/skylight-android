package se.gustavkarlsson.skylight.android.caching

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer
import se.gustavkarlsson.skylight.android.entities.AuroraReport

internal class AuroraReportCacheSerializer : CacheSerializer<AuroraReport> {
	private val gson = GsonBuilder()
		.registerTypeAdapter(InstantTypeAdapter())
		.create()

	override fun fromString(json: String): AuroraReport = gson.fromJson(json)

	override fun toString(report: AuroraReport): String = gson.toJson(report)

	private inline fun <reified T : Any> Gson.fromJson(json: String): T =
		this.fromJson(json, T::class.java)

	private inline fun <reified T : Any> GsonBuilder.registerTypeAdapter(typeAdapter: TypeAdapter<T>): GsonBuilder =
		this.registerTypeAdapter(T::class.java, typeAdapter)
}
