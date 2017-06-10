package se.gustavkarlsson.skylight.android.settings

interface DebugSettings {
    val isOverrideValues: Boolean
    val kpIndex: Float
    val geomagLatitude: Float
    val sunZenithAngle: Float
    val cloudPercentage: Int
}
