package se.gustavkarlsson.skylight.android.cache

import com.google.gson.Gson
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer

internal class GsonCacheSerializer<T>(private val clazz: Class<T>) : CacheSerializer<T> {
    private val gson = Gson()

    override fun fromString(json: String): T {
        return gson.fromJson(json, clazz)
    }

    override fun toString(report: T): String {
        return gson.toJson(report)
    }
}
