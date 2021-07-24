package se.gustavkarlsson.skylight.android

internal interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    interface Setter {
        fun setMainActivityComponent(component: MainActivityComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: MainActivityComponent
            private set
    }
}
