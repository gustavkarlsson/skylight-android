package se.gustavkarlsson.skylight.android.caching

import android.content.Context
import com.vincentbrison.openlibraries.android.dualcache.Builder
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer
import com.vincentbrison.openlibraries.android.dualcache.DualCache
import se.gustavkarlsson.skylight.android.services.SingletonCache

internal class DualSingletonCache<T>(
	context: Context,
	cacheId: String,
	defaultValue: T,
	serializer: CacheSerializer<T>
) : SingletonCache<T> {
	private val dualCache: DualCache<T>

	init {
		dualCache = Builder<T>(cacheId, BuildConfig.VERSION_CODE)
			.useReferenceInRam(Integer.MAX_VALUE, { 1 })
			.useSerializerInDisk(Integer.MAX_VALUE, false, serializer, context)
			.apply {
				if (BuildConfig.DEBUG) {
					enableLog()
				}
			}
			.build()
			.apply {
				if (!contains(KEY)) {
					put(KEY, defaultValue)
				}
			}
	}

	override var value: T
		get() = dualCache.get(KEY)
		set(value) = dualCache.put(KEY, value)

	companion object {
		private const val KEY = "singleton" // Must match [a-z0-9_-]{1,64}
	}
}
