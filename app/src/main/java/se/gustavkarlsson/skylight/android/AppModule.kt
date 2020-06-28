package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Locale
import javax.inject.Named

@Module
internal class AppModule(private val application: Application) {

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
    fun singleLocale(context: Context): Single<Locale> =
        Single.fromCallable {
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
    fun mainThreadScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Reusable
    @Io
    fun ioScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Reusable
    @Computation
    fun computationScheduler(): Scheduler = Schedulers.computation()
}
