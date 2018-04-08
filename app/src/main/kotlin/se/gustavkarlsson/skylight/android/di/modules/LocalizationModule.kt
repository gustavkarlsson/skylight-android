package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Single
import java.util.*

interface LocalizationModule {
    val locale: Single<Locale>
}
