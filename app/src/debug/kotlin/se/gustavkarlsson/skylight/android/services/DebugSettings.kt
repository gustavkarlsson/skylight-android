package se.gustavkarlsson.skylight.android.services

interface DebugSettings {
    val isOverrideValues: Boolean
    val kpIndex: Double
    val geomagLatitude: Double
    val sunZenithAngle: Double
    val cloudPercentage: Int
}
