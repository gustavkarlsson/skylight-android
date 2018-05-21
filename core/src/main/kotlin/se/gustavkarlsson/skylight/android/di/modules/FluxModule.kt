package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.flux.SkylightStore

interface FluxModule {
    val store: SkylightStore
}
