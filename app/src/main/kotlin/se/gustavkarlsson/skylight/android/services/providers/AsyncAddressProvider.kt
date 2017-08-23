package se.gustavkarlsson.skylight.android.services.providers

import android.location.Address

import java.util.concurrent.Future

interface AsyncAddressProvider {
    fun execute(latitude: Double, longitude: Double): Future<Address?>
}