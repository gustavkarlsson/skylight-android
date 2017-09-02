package se.gustavkarlsson.skylight.android.services_impl.cache

import android.content.Context
import com.vincentbrison.openlibraries.android.dualcache.Builder
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer
import com.vincentbrison.openlibraries.android.dualcache.DualCache
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.services.SingletonCache

class DualSingletonCache<T>(
		cacheId: String,
		defaultValue: T,
		serializer: CacheSerializer<T>,
		context: Context
) : SingletonCache<T> {
	private val dualCache: DualCache<T>

	init {
		val builder = Builder<T>(cacheId, BuildConfig.VERSION_CODE)
			.useReferenceInRam(Integer.MAX_VALUE, { 1 })
			.useSerializerInDisk(Integer.MAX_VALUE, false, serializer, context)
		if (BuildConfig.DEBUG) {
			builder.enableLog()
		}
		dualCache = builder.build()
		if (!dualCache.contains(KEY)) {
			dualCache.put(KEY, defaultValue)
		}
	}

	override var value: T
		get() = dualCache.get(KEY)
		set(value) = dualCache.put(KEY, value)

	companion object {
		private val KEY = "singleton" // Must match [a-z0-9_-]{1,64}
	}
}
