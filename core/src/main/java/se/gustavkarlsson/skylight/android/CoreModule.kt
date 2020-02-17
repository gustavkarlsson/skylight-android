package se.gustavkarlsson.skylight.android

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.Time
import se.gustavkarlsson.skylight.android.time.SystemTime

val coreModule = module {

    single<Time> {
        SystemTime
    }
}
