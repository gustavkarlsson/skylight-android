package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.app.Application
import android.content.Context
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
import java.util.Locale
import javax.inject.Named

@Module
internal class AppModule(private val application: Application) {

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
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
    fun getLocale(context: Context): () -> Locale = { context.resources.configuration.locales[0] }

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
