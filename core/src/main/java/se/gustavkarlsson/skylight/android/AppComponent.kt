package se.gustavkarlsson.skylight.android

interface AppComponent {

    interface Setter
}

@Suppress("unused")
fun AppComponent.Setter.setAppComponent(component: AppComponent) {
    appComponent = component
}

lateinit var appComponent: AppComponent
    private set
