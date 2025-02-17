package se.gustavkarlsson.skylight.android.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import java.util.Locale

@Component
@CoreScope
abstract class CoreComponent internal constructor(
    @get:Provides @get:CoreScope val application: Application,
    @get:Provides @get:VersionCode val versionCode: Int,
    @get:Provides @get:VersionName val versionName: String,
    @get:Provides val activityClass: Class<out Activity>,
) {
    val context: Context
        @Provides get() = application

    @OptIn(DelicateCoroutinesApi::class)
    @get:Provides
    @get:Global
    val globalScope: CoroutineScope = GlobalScope

    val mainDispatcher: CoroutineDispatcher
        @Provides
        @Main
        get() = Dispatchers.Main

    val ioDispatcher: CoroutineDispatcher
        @Provides
        @Io
        get() = Dispatchers.IO

    abstract val getLocales: () -> NonEmptyList<Locale>

    abstract val getLocale: () -> Locale

    @Provides
    internal fun getLocales(context: Context): () -> NonEmptyList<Locale> = {
        val locales = context.resources.configuration.locales
        require(!locales.isEmpty) { "locales should not be empty" }
        val list = buildList {
            for (i in 0 until locales.size()) {
                add(locales[i])
            }
        }
        list.toNonEmptyListOrNull() ?: throw IndexOutOfBoundsException("Empty list")
    }

    @Provides
    internal fun getLocale(getLocales: () -> NonEmptyList<Locale>): () -> Locale = {
        getLocales().head
    }

    companion object {
        @SuppressLint("StaticFieldLeak") // App context is okay
        lateinit var instance: CoreComponent
    }
}
