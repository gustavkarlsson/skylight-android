package se.gustavkarlsson.skylight.android.lib.time

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class TimeComponent {

    @get:Provides
    val time: Time = SystemTime

    companion object {
        val instance: TimeComponent = TimeComponent::class.create()
    }
}
