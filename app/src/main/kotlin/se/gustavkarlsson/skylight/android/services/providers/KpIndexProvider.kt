package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.entities.KpIndex

interface KpIndexProvider {
    suspend fun getKpIndex(): KpIndex
}
