package se.gustavkarlsson.skylight.android.lib.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides

@Module
object LibAnalyticsModule {

    // FIXME clean up
    @Provides
    internal fun analytics(context: Context): Analytics =
        FirebasedAnalytics(FirebaseAnalytics.getInstance(context))
}
