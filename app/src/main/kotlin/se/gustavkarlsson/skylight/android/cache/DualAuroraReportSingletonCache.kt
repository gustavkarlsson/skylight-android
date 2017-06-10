package se.gustavkarlsson.skylight.android.cache

import android.content.Context
import com.vincentbrison.openlibraries.android.dualcache.Builder
import com.vincentbrison.openlibraries.android.dualcache.DualCache
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.models.AuroraReport

private const val KEY = "singleton" // Must match [a-z0-9_-]{1,64}

class DualAuroraReportSingletonCache(context: Context, cacheId: String) : SingletonCache<AuroraReport> {
    private val dualCache: DualCache<AuroraReport>

    init {
        val builder = Builder<AuroraReport>(cacheId, BuildConfig.VERSION_CODE)
                .useReferenceInRam(Integer.MAX_VALUE) { 1 }
                .useSerializerInDisk(Integer.MAX_VALUE, false, GsonCacheSerializer(AuroraReport::class.java), context)
        if (BuildConfig.DEBUG) {
            builder.enableLog()
        }
        this.dualCache = builder.build()
    }

    override var value: AuroraReport?
        get() = dualCache.get(KEY)
        set(report) = dualCache.put(KEY, report)

}
