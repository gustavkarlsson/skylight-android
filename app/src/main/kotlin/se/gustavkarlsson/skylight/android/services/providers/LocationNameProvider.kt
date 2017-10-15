package se.gustavkarlsson.skylight.android.services.providers

interface LocationNameProvider {
    suspend fun getLocationName(latitude: Double, longitude: Double): String?
}
