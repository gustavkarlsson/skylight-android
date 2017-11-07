package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.entities.KpIndex

interface KpIndexProvider {
    fun getKpIndex(): KpIndex
}
