package se.gustavkarlsson.skylight.android.lib.time

import se.gustavkarlsson.skylight.android.services.Time

interface TimeComponent {

    fun time(): Time

    interface Setter {
        fun setTimeComponent(component: TimeComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: TimeComponent
            private set
    }
}
