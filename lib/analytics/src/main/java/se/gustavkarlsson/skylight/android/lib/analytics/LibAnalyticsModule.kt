package se.gustavkarlsson.skylight.android.lib.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@Module
@ContributesTo(AppScopeMarker::class)
object LibAnalyticsModule {

    @Provides
    internal fun analytics(context: Context): Analytics =
        FirebasedAnalytics(FirebaseAnalytics.getInstance(context))
}
