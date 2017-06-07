package se.gustavkarlsson.skylight.android.settings

internal interface DebugSettings {
    val isOverrideValues: Boolean
    val kpIndex: Float
    val geomagLatitude: Float
    val sunZenithAngle: Float
    val cloudPercentage: Int
}
