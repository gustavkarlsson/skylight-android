package se.gustavkarlsson.skylight.android.background.providers

import android.location.Address

import java.util.concurrent.Future

internal interface AsyncAddressProvider {
    fun execute(latitude: Double, longitude: Double): Future<Address?>
}
