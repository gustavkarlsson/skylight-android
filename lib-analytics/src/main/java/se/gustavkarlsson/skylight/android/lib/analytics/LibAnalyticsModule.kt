package se.gustavkarlsson.skylight.android.lib.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Analytics

val libAnalyticsModule = module {

    single {
        FirebaseAnalytics.getInstance(get())
    }

    single<Analytics> {
        FirebasedAnalytics(firebaseAnalytics = get())
    }
}

@Module
class LibAnalyticsModule {

    @Provides
    @Reusable
    internal fun analytics(context: Context): Analytics =
        FirebasedAnalytics(FirebaseAnalytics.getInstance(context))
}
