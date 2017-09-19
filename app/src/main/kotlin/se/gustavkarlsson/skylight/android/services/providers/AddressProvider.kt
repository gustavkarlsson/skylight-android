package se.gustavkarlsson.skylight.android.services.providers

import android.location.Address

interface AddressProvider {
    suspend fun getAddress(latitude: Double, longitude: Double): Address?
}
