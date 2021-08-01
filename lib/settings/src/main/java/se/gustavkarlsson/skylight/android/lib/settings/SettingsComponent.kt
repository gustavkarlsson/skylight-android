package se.gustavkarlsson.skylight.android.lib.settings

interface SettingsComponent {

    fun settings(): SettingsRepository

    interface Setter {
        fun setSettingsComponent(component: SettingsComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: SettingsComponent
            private set
    }
}
