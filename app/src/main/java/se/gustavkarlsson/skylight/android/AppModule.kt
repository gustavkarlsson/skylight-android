package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.dsl.module.module
import java.util.Locale

internal val appModule = module {

    scope("activity") { (fragmentActivity: FragmentActivity) ->
        fragmentActivity
    }

    scope("activity") {
        get<FragmentActivity>() as Activity
    }

    scope("activity") {
        get<FragmentActivity>().supportFragmentManager
    }

    scope("activity") {
        get<Activity>().fragmentContainer as ViewGroup
    }

    single<Class<out Activity>>("activity") {
        MainActivity::class.java
    }

    single<Single<Locale>>("locale") {
        val context = get<Context>()
        Single.fromCallable {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale
            }
        }
    }

    single("versionCode") {
        BuildConfig.VERSION_CODE
    }

    single("versionName") {
        BuildConfig.VERSION_NAME
    }
}

@Module
internal class AppModule(private val application: Application) {

    @Provides
    @Reusable
    fun application(): Application = application

    @Provides
    @Reusable
    fun context(): Context = application
}
