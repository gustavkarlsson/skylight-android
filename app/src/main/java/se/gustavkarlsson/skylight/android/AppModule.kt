package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.app.Application
import android.content.Context
import arrow.core.NonEmptyList
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.Main
import se.gustavkarlsson.skylight.android.core.VersionCode
import se.gustavkarlsson.skylight.android.core.VersionName
import java.util.Locale

@Module
internal class AppModule(private val application: Application) {

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @Global
    fun scope(): CoroutineScope = GlobalScope

    @Provides
    fun application(): Application = application

    @Provides
    fun context(): Context = application

    @Provides
    fun activityClass(): Class<out Activity> = MainActivity::class.java

    @Provides
    fun getLocale(getLocales: () -> NonEmptyList<Locale>): () -> Locale = { getLocales().head }

    @Provides
    @Reusable
    fun getLocales(context: Context): () -> NonEmptyList<Locale> = {
        val locales = context.resources.configuration.locales
        require(!locales.isEmpty) { "locales should not be empty" }
        val list = buildList {
            for (i in 0 until locales.size()) {
                add(locales[i])
            }
        }
        NonEmptyList.fromListUnsafe(list)
    }

    @Provides
    @VersionCode
    fun versionCode(): Int = BuildConfig.VERSION_CODE

    @Provides
    @VersionName
    fun versionName(): String = BuildConfig.VERSION_NAME

    @Provides
    @Main
    fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Io
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
