package se.gustavkarlsson.skylight.android.lib.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.Analytics

@Module
class LibAnalyticsModule {

    @Provides
    @Reusable
    internal fun analytics(context: Context): Analytics =
        FirebasedAnalytics(FirebaseAnalytics.getInstance(context))
}
