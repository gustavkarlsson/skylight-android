package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.Global
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.Main
import java.util.Locale
import javax.inject.Named

@Module
internal class AppModule(private val application: Application) {

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @AppScope
    @Global
    fun scope(): CoroutineScope = GlobalScope

    @Provides
    @Reusable
    fun application(): Application = application

    @Provides
    @Reusable
    fun context(): Context = application

    @Provides
    @Reusable
    fun activityClass(): Class<out Activity> = MainActivity::class.java

    @Provides
    @Reusable
    fun getLocale(context: Context): () -> Locale =
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale
            }
        }

    @Provides
    @Reusable
    @Named("versionCode")
    fun versionCode(): Int = BuildConfig.VERSION_CODE

    @Provides
    @Reusable
    @Named("versionName")
    fun versionName(): String = BuildConfig.VERSION_NAME

    @Provides
    @Reusable
    @Main
    fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Reusable
    @Io
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
