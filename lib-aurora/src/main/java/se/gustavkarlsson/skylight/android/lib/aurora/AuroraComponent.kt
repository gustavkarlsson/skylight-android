package se.gustavkarlsson.skylight.android.lib.aurora

interface AuroraComponent {

    fun auroraReportProvider(): AuroraReportProvider

    interface Setter {
        fun setAuroraComponent(component: AuroraComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: AuroraComponent
            private set
    }
}
