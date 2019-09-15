package se.gustavkarlsson.skylight.android.lib.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.dsl.module.module

val libAnalyticsModule = module {

	single {
		FirebaseAnalytics.getInstance(get())
	}

	single<Analytics> {
		FirebasedAnalytics(firebaseAnalytics = get())
	}
}