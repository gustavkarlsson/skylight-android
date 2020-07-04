package se.gustavkarlsson.skylight.android.lib.runversion

interface RunVersionComponent {

    fun runVersionManager(): RunVersionManager

    interface Setter {
        fun setRunVersionComponent(component: RunVersionComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: RunVersionComponent
            private set
    }
}
