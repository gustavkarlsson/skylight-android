package se.gustavkarlsson.skylight.android.feature.background

import io.reactivex.Completable
import javax.inject.Named

interface BackgroundComponent {

    @Named("notify")
    fun notifyWork(): Completable

    interface Setter {
        fun setBackgroundComponent(component: BackgroundComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: BackgroundComponent
            private set
    }
}
